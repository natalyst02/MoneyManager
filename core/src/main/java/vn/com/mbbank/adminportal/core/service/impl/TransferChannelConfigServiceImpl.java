package vn.com.mbbank.adminportal.core.service.impl;

import com.dslplatform.json.JsonWriter;
import com.dslplatform.json.runtime.Generics;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import vn.com.mbbank.adminportal.common.exception.PaymentPlatformException;
import vn.com.mbbank.adminportal.common.model.OpType;
import vn.com.mbbank.adminportal.common.model.SyncMessage;
import vn.com.mbbank.adminportal.common.service.internal.EventMessageServiceInternal;
import vn.com.mbbank.adminportal.common.util.CompletableFutures;
import vn.com.mbbank.adminportal.common.util.Json;
import vn.com.mbbank.adminportal.core.mapper.*;
import vn.com.mbbank.adminportal.core.model.PriorityReference;
import vn.com.mbbank.adminportal.core.model.message.UpdateTransferChannelConfigPrioritiesMessage;
import vn.com.mbbank.adminportal.core.model.message.UpdateTransferChannelConfigStatusMessage;
import vn.com.mbbank.adminportal.core.model.request.TransferChannelConfigsFilter;
import vn.com.mbbank.adminportal.core.model.request.UpdateTransferChannelConfigPrioritiesRequest;
import vn.com.mbbank.adminportal.core.model.request.UpdateTransferChannelConfigStatusRequest;
import vn.com.mbbank.adminportal.core.model.response.GetTransferChannelConfigResponse;
import vn.com.mbbank.adminportal.core.model.response.UpdateTransferChannelConfigPrioritiesResponse;
import vn.com.mbbank.adminportal.core.model.response.UpdateTransferChannelConfigStatusResponse;
import vn.com.mbbank.adminportal.core.repository.TransferChannelConfigRepository;
import vn.com.mbbank.adminportal.core.service.TransferChannelConfigService;
import vn.com.mbbank.adminportal.core.util.Authentications;
import vn.com.mbbank.adminportal.core.util.ErrorCode;
import vn.com.mbbank.adminportal.core.util.Filters;
import vn.com.mbbank.adminportal.core.util.MessageHelper;
import vn.com.mbbank.adminportal.core.validator.UpdateTransferChannelConfigPrioritiesValidator;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Log4j2
public class TransferChannelConfigServiceImpl implements TransferChannelConfigService {
  private final TransferChannelConfigRepository transferChannelConfigRepository;
  private final EventMessageServiceInternal eventMessageService;
  private final UpdateTransferChannelConfigPrioritiesValidator updateTransferChannelConfigPrioritiesValidator;
  private final JsonWriter.WriteObject<SyncMessage<UpdateTransferChannelConfigStatusMessage>> syncMessageUpdateStatusWriter = Json.findWriter(Generics.makeParameterizedType(SyncMessage.class, UpdateTransferChannelConfigStatusMessage.class));
  private final JsonWriter.WriteObject<SyncMessage<UpdateTransferChannelConfigPrioritiesMessage>> syncMessageUpdatePrioritiesWriter = Json.findWriter(Generics.makeParameterizedType(SyncMessage.class, UpdateTransferChannelConfigPrioritiesMessage.class));
  @Value("${transfer-channel-config.topic}")
  private String transferChannelConfigTopic;

  @Override
  public List<GetTransferChannelConfigResponse> getTransferChannelConfigs(Authentication authentication, TransferChannelConfigsFilter request) {
    Authentications.requireUsername(authentication);
    var transferChannel = request.getTransferChannel();
    var transferType = request.getTransferType();
    if (transferChannel != null && transferType != null && transferChannel.getTransferType() != transferType) {
      return Collections.emptyList();
    }
    return TransferChannelConfig2GetTransferChannelConfigRespMapper.INSTANCE.map(transferChannelConfigRepository.findAll(Filters.toSpecification(request)));
  }

  @Override
  public CompletableFuture<UpdateTransferChannelConfigStatusResponse> updateStatus(Authentication authentication, UpdateTransferChannelConfigStatusRequest request) {
    var username = Authentications.requireUsername(authentication);
    var id = request.getId();
    var transferChannelConfig = transferChannelConfigRepository.getLocked(id)
        .orElseThrow(() -> new PaymentPlatformException(ErrorCode.TRANSFER_CHANNEL_CONFIG_NOT_FOUND, "Transfer channel config with id " + id + " not found"));
    var active = request.getActive();
    if (transferChannelConfig.isActive() == Boolean.TRUE.equals(active)) {
      throw new PaymentPlatformException(ErrorCode.STATUS_NOT_ALLOWED, "Transfer channel config status is already " + (Boolean.TRUE.equals(active) ? "active" : "inactive"));
    }
    var now = OffsetDateTime.now();
    var updatedTransferChannelConfig = transferChannelConfigRepository.updateStatus(request, username, now)
        .orElseThrow(() -> new PaymentPlatformException(ErrorCode.UPDATE_TRANSFER_CHANNEL_CONFIG_FAIL, ErrorCode.UPDATE_TRANSFER_CHANNEL_CONFIG_FAIL.message()));
    var syncMessage = MessageHelper.toSyncMessage(OpType.U, TransferChannelConfig2UpdateTransferChannelConfigStatusMessageMapper.INSTANCE.map(updatedTransferChannelConfig), now, "STATUS");
    var eventMessage = eventMessageService.save(transferChannelConfigTopic, updatedTransferChannelConfig.getId().toString(), username, Json.encode(syncMessage, syncMessageUpdateStatusWriter));
    return eventMessageService.sendAndUpdate9093Async(eventMessage)
        .thenApply(unused -> TransferChannelConfig2UpdateTransferChannelConfigStatusRespMapper.INSTANCE.map(updatedTransferChannelConfig))
        .exceptionally(throwable -> {
          throw CompletableFutures.toCompletionException(new PaymentPlatformException(ErrorCode.SEND_AND_UPDATE_EVENT_ERROR, ErrorCode.SEND_AND_UPDATE_EVENT_ERROR.message()));
        });
  }

  @Override
  public CompletableFuture<UpdateTransferChannelConfigPrioritiesResponse> updatePriorities(Authentication authentication, UpdateTransferChannelConfigPrioritiesRequest request) {
    updateTransferChannelConfigPrioritiesValidator.validate(request);
    var username = Authentications.requireUsername(authentication);
    var priorityReferences = request.getPriorities();
    var now = OffsetDateTime.now();
    var ids = priorityReferences.stream().map(PriorityReference::getId).toList();
    var priorities = priorityReferences.stream().map(PriorityReference::getPriority).toList();
    var transferChannelReferences = transferChannelConfigRepository.getAll();
    var transferChannelReferenceOpt = transferChannelReferences.stream()
        .filter(transferChannelReference -> !ids.contains(transferChannelReference.getId()))
        .filter(transferChannelReference -> priorities.contains(transferChannelReference.getPriority()))
        .findAny();
    if (transferChannelReferenceOpt.isPresent()) {
      throw new PaymentPlatformException(ErrorCode.DUPLICATE_TRANSFER_CHANNEL_CONFIG_PRIORITY, "Transfer channel config with priority: " + transferChannelReferenceOpt.get().getPriority() + " already exits in database");
    }
    var updatedResult = transferChannelConfigRepository.updatePriorities(request, username, now);
    if (Arrays.stream(updatedResult).anyMatch(value -> value == 0)) {
      throw new PaymentPlatformException(ErrorCode.TRANSFER_CHANNEL_CONFIG_NOT_FOUND, "Some transfer channel configs do not exist");
    }
    var syncMessage = MessageHelper.toSyncMessage(OpType.U, UpdateTransferChannelConfigPrioritiesReq2UpdateTransferChannelPrioritiesMessageMapper.INSTANCE.map(request, transferChannelReferences, username, now), now, "PRIORITIES");
    var eventMessage = eventMessageService.save(transferChannelConfigTopic, getKey(priorityReferences), username, Json.encode(syncMessage, syncMessageUpdatePrioritiesWriter));
    return eventMessageService.sendAndUpdate9093Async(eventMessage)
        .thenApply(unused -> UpdateTransferChannelConfigPrioritiesReq2UpdateTransferChannelConfigPrioritiesRespMapper.INSTANCE.map(request, username, now))
        .exceptionally(throwable -> {
          throw CompletableFutures.toCompletionException(new PaymentPlatformException(ErrorCode.SEND_AND_UPDATE_EVENT_ERROR, ErrorCode.SEND_AND_UPDATE_EVENT_ERROR.message()));
        });
  }

  private String getKey(List<PriorityReference> priorityReferences) {
    var key = new StringBuilder();
    priorityReferences.forEach(priorityReference -> key.append(priorityReference.getId()).append("-"));
    if (!key.isEmpty()) {
      key.deleteCharAt(key.length() - 1);
    }
    return key.toString();
  }
}
