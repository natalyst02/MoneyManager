package vn.com.mbbank.adminportal.core.service.impl;

import com.dslplatform.json.JsonWriter;
import com.dslplatform.json.runtime.Generics;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import vn.com.mbbank.adminportal.common.exception.NSTCompletionException;
import vn.com.mbbank.adminportal.common.exception.PaymentPlatformException;
import vn.com.mbbank.adminportal.common.model.OpType;
import vn.com.mbbank.adminportal.common.model.SyncMessage;
import vn.com.mbbank.adminportal.common.service.internal.EventMessageServiceInternal;
import vn.com.mbbank.adminportal.common.util.CompletableFutures;
import vn.com.mbbank.adminportal.common.util.Json;
import vn.com.mbbank.adminportal.common.util.Pageables;
import vn.com.mbbank.adminportal.core.mapper.*;
import vn.com.mbbank.adminportal.core.model.entity.TransferChannelBankConfig;
import vn.com.mbbank.adminportal.core.model.filter.TransferChannelBankConfigFilter;
import vn.com.mbbank.adminportal.core.model.filter.TransferChannelBankConfigHistoryFilter;
import vn.com.mbbank.adminportal.core.model.message.TransferChannelBankConfigMessage;
import vn.com.mbbank.adminportal.core.model.request.CreateTransferChannelBankConfigRequest;
import vn.com.mbbank.adminportal.core.model.request.UpdateTransferChannelBankConfigRequest;
import vn.com.mbbank.adminportal.core.model.response.TransferChannelBankConfigHistoryResponse;
import vn.com.mbbank.adminportal.core.model.response.TransferChannelBankConfigResponse;
import vn.com.mbbank.adminportal.core.repository.TransferChannelBankConfigHistoryRepository;
import vn.com.mbbank.adminportal.core.repository.TransferChannelBankConfigRepository;
import vn.com.mbbank.adminportal.core.service.TransferChannelBankConfigService;
import vn.com.mbbank.adminportal.core.util.Authentications;
import vn.com.mbbank.adminportal.core.util.ErrorCode;
import vn.com.mbbank.adminportal.core.util.Filters;
import vn.com.mbbank.adminportal.core.util.MessageHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static vn.com.mbbank.adminportal.core.util.ErrorCode.TRANSFER_CHANNEL_BANK_CONFIG_EXISTED;

@Service
@RequiredArgsConstructor
@Log4j2
public class TransferChannelBankConfigServiceImpl implements TransferChannelBankConfigService {
  private static final JsonWriter.WriteObject<SyncMessage<List<TransferChannelBankConfigMessage>>> TRANSFER_CHANNEL_BANK_CONFIG_CREATE_WRITER =
          Json.findWriter(Generics.makeParameterizedType(SyncMessage.class,
                  Generics.makeParameterizedType(List.class, TransferChannelBankConfigMessage.class)));
  private static final JsonWriter.WriteObject<SyncMessage<TransferChannelBankConfigMessage>> TRANSFER_CHANNEL_BANK_CONFIG_UPDATE_WRITER =
          Json.findWriter(Generics.makeParameterizedType(SyncMessage.class, TransferChannelBankConfigMessage.class));
  private final TransferChannelBankConfigRepository transferChannelBankConfigRepository;
  private final TransferChannelBankConfigHistoryRepository transferChannelBankConfigHistoryRepository;
  private final EventMessageServiceInternal eventMessageService;
  @Value("${transfer-channel-bank-config.topic}")
  private String transferChannelBankConfigTopic;

  @Override
  public TransferChannelBankConfigResponse getTransferChannelBankConfig(Authentication authentication, Long id) {
    Authentications.requirePapUser(authentication);
    var entity = transferChannelBankConfigRepository.findById(id)
        .orElseThrow(() -> new PaymentPlatformException(ErrorCode.TRANSFER_CHANNEL_BANK_CONFIG_NOT_FOUND, ErrorCode.TRANSFER_CHANNEL_BANK_CONFIG_NOT_FOUND.message()));
    return TransferChannelBankConfig2ResponseMapper.INSTANCE.map(entity);
  }

  @Override
  public Page<TransferChannelBankConfigResponse> getTransferChannelBankConfigs(Authentication authentication, TransferChannelBankConfigFilter filter) {
    Authentications.requirePapUser(authentication);
    var pageable = Pageables.of(filter.getPage(), filter.getSize(), filter.getSort());
    return transferChannelBankConfigRepository.findAll(Filters.toSpecification(filter), pageable)
        .map(TransferChannelBankConfig2ResponseMapper.INSTANCE::map);
  }

  @Override
  public CompletableFuture<List<TransferChannelBankConfigResponse>> create(Authentication authentication, CreateTransferChannelBankConfigRequest request) {
    var username = Authentications.requireUsername(authentication);

    List<Long> ids = new ArrayList<>();
    var transferChannelBankConfigs = create(CreateTransferChannelBankConfigRequest2TransferChannelBankConfigMapper.INSTANCE.toEntityList(request, username));
    var listMessageData = transferChannelBankConfigs.stream()
            .map(c -> {
              ids.add(c.getId());
              return TransferChannelBankConfig2MessageMapper.INSTANCE.map(c);
            })
            .toList();

    var syncMessage = MessageHelper.toSyncMessage(OpType.I, listMessageData);
    var eventMessage = eventMessageService.save(transferChannelBankConfigTopic, ids.toString(),
        username, Json.encode(syncMessage, TRANSFER_CHANNEL_BANK_CONFIG_CREATE_WRITER));
    return eventMessageService.sendAndUpdate9093Async(eventMessage)
            .thenApply((Void) -> transferChannelBankConfigs.stream()
                    .map(TransferChannelBankConfig2ResponseMapper.INSTANCE::map)
                    .toList())
            .exceptionally(throwable -> {
              throw CompletableFutures.toCompletionException(new PaymentPlatformException(
                      ErrorCode.SEND_AND_UPDATE_EVENT_ERROR,
                      ErrorCode.SEND_AND_UPDATE_EVENT_ERROR.message()
              ));
            });
  }

  private List<TransferChannelBankConfig> create(List<TransferChannelBankConfig> transferChannelBankConfigs) {
    try {
      return transferChannelBankConfigRepository.persistAllAndFlush(transferChannelBankConfigs);
    } catch (Exception ex) {
      log.error("Create transfer channel bank config fail", ex);
      if (ex.getCause() instanceof ConstraintViolationException cve && cve.getConstraintName() != null
          && (cve.getConstraintName().endsWith("PAP_TRANSFER_CHANNEL_BANK_CONFIG_CARD_BIN_BANK_CODE_TRANSFER_CHANNEL_UINDEX"))) {
        throw new PaymentPlatformException(TRANSFER_CHANNEL_BANK_CONFIG_EXISTED, TRANSFER_CHANNEL_BANK_CONFIG_EXISTED.message());
      }
      throw new PaymentPlatformException(ErrorCode.CREATE_TRANSFER_CHANNEL_BANK_CONFIG_FAIL, ErrorCode.CREATE_TRANSFER_CHANNEL_BANK_CONFIG_FAIL.message());
    }
  }

  @Override
  public CompletableFuture<TransferChannelBankConfigResponse> update(Authentication authentication, UpdateTransferChannelBankConfigRequest request) {
    var username = Authentications.requireUsername(authentication);
    var entity = transferChannelBankConfigRepository.getLockedTransferChannelBankConfig(request.getId())
        .orElseThrow(() -> new NSTCompletionException(new PaymentPlatformException(ErrorCode.TRANSFER_CHANNEL_BANK_CONFIG_NOT_FOUND,
            ErrorCode.TRANSFER_CHANNEL_BANK_CONFIG_NOT_FOUND.message())));

    var transferChannelBankConfig = update(UpdateTransferChannelBankConfigRequest2EntityMapper.INSTANCE.map(request, username, entity));
    var syncMessage = MessageHelper.toSyncMessage(OpType.U, TransferChannelBankConfig2MessageMapper.INSTANCE.map(transferChannelBankConfig));
    var eventMessage = eventMessageService.save(transferChannelBankConfigTopic, transferChannelBankConfig.getId().toString(),
        username, Json.encode(syncMessage, TRANSFER_CHANNEL_BANK_CONFIG_UPDATE_WRITER));
    return eventMessageService.sendAndUpdate9093Async(eventMessage)
        .thenApply((Void) -> TransferChannelBankConfig2ResponseMapper.INSTANCE.map(transferChannelBankConfig))
        .exceptionally(throwable -> {
          throw CompletableFutures.toCompletionException(new PaymentPlatformException(ErrorCode.SEND_AND_UPDATE_EVENT_ERROR, ErrorCode.SEND_AND_UPDATE_EVENT_ERROR.message()));
        });
  }

  private TransferChannelBankConfig update(TransferChannelBankConfig transferChannelBankConfig) {
    try {
      return transferChannelBankConfigRepository.mergeAndFlush(transferChannelBankConfig);
    } catch (Exception ex) {
      if (ex.getCause() instanceof ConstraintViolationException cve && cve.getConstraintName() != null
          && (cve.getConstraintName().endsWith("PAP_TRANSFER_CHANNEL_BANK_CONFIG_CARD_BIN_BANK_CODE_TRANSFER_CHANNEL_UINDEX"))) {
        throw new PaymentPlatformException(TRANSFER_CHANNEL_BANK_CONFIG_EXISTED, TRANSFER_CHANNEL_BANK_CONFIG_EXISTED.message());
      }
      throw new PaymentPlatformException(ErrorCode.UPDATE_TRANSFER_CHANNEL_BANK_CONFIG_FAIL, ErrorCode.UPDATE_TRANSFER_CHANNEL_BANK_CONFIG_FAIL.message());
    }
  }

  @Override
  public TransferChannelBankConfigHistoryResponse getTransferChannelBankHistory(Authentication authentication, Long historyId) {
    Authentications.requirePapUser(authentication);
    var entity = transferChannelBankConfigHistoryRepository.findById(historyId)
        .orElseThrow(() -> new PaymentPlatformException(ErrorCode.TRANSFER_CHANNEL_BANK_CONFIG_HISTORY_NOT_FOUND, ErrorCode.TRANSFER_CHANNEL_BANK_CONFIG_HISTORY_NOT_FOUND.message()));
    return TransferChannelBankConfigHistory2ResponseMapper.INSTANCE.map(entity);
  }

  @Override
  public Page<TransferChannelBankConfigHistoryResponse> getTransferChannelBankHistory(Authentication authentication, TransferChannelBankConfigHistoryFilter filter) {
    Authentications.requirePapUser(authentication);
    var pageable = Pageables.of(filter.getPage(), filter.getSize(), filter.getSort());
    return transferChannelBankConfigHistoryRepository.findAll(Filters.toSpecification(filter), pageable)
        .map(TransferChannelBankConfigHistory2ResponseMapper.INSTANCE::map);
  }
}
