package vn.com.mbbank.adminportal.core.service.impl;

import com.dslplatform.json.JsonWriter;
import com.dslplatform.json.runtime.Generics;
import lombok.extern.log4j.Log4j2;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import vn.com.mbbank.adminportal.common.exception.PaymentPlatformException;
import vn.com.mbbank.adminportal.common.model.OpType;
import vn.com.mbbank.adminportal.common.model.SyncMessage;
import vn.com.mbbank.adminportal.common.service.internal.EventMessageServiceInternal;
import vn.com.mbbank.adminportal.common.util.*;
import vn.com.mbbank.adminportal.core.mapper.*;
import vn.com.mbbank.adminportal.core.model.entity.TransferChannelLimit;
import vn.com.mbbank.adminportal.core.model.filter.TransferChannelLimitFilter;
import vn.com.mbbank.adminportal.core.model.filter.TransferChannelLimitHistoryFilter;
import vn.com.mbbank.adminportal.core.model.message.TransferChannelLimitMessage;
import vn.com.mbbank.adminportal.core.model.request.CreateTransferChannelLimitRequest;
import vn.com.mbbank.adminportal.core.model.request.UpdateTransferChannelLimitRequest;
import vn.com.mbbank.adminportal.core.model.response.TransferChannelLimitHistoryResp;
import vn.com.mbbank.adminportal.core.model.response.TransferChannelLimitResp;
import vn.com.mbbank.adminportal.core.repository.TransferChannelLimitRepository;
import vn.com.mbbank.adminportal.core.service.TransferChannelLimitHistoryService;
import vn.com.mbbank.adminportal.core.service.internal.TransferChannelLimitServiceInternal;
import vn.com.mbbank.adminportal.core.util.Authentications;
import vn.com.mbbank.adminportal.core.util.ErrorCode;
import vn.com.mbbank.adminportal.core.util.Filters;
import vn.com.mbbank.adminportal.core.util.MessageHelper;
import vn.com.mbbank.adminportal.core.validator.CreateTransferChannelLimitValidator;
import vn.com.mbbank.adminportal.core.validator.UpdateTransferChannelLimitValidator;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;


@Service
@Log4j2
public class TransferChannelLimitServiceImpl implements TransferChannelLimitServiceInternal {
    private final TransferChannelLimitHistoryService transferChannelLimitHistoryService;
    private final TransferChannelLimitRepository transferChannelLimitRepository;
    private final CreateTransferChannelLimitValidator createTransferChannelLimitValidator;
    private final UpdateTransferChannelLimitValidator updateTransferChannelLimitValidator;
    private final EventMessageServiceInternal eventMessageServiceInternal;
    private final String limitTopic;
    private final JsonWriter.WriteObject<SyncMessage<TransferChannelLimitMessage>> TRANSFER_CHANNEL_LIMIT_WRITER = Json.findWriter(Generics.makeParameterizedType(SyncMessage.class, TransferChannelLimitMessage.class));

    public TransferChannelLimitServiceImpl(
            TransferChannelLimitHistoryService transferChannelLimitHistoryService,
            TransferChannelLimitRepository transferChannelLimitRepository,
            CreateTransferChannelLimitValidator createTransferChannelLimitValidator,
            UpdateTransferChannelLimitValidator updateTransferChannelLimitValidator,
            EventMessageServiceInternal eventMessageServiceInternal,
            @Value("${transfer-channel-limit.topic}") String limitTopic) {
        this.transferChannelLimitHistoryService = transferChannelLimitHistoryService;
        this.transferChannelLimitRepository = transferChannelLimitRepository;
        this.createTransferChannelLimitValidator = createTransferChannelLimitValidator;
        this.updateTransferChannelLimitValidator = updateTransferChannelLimitValidator;
        this.eventMessageServiceInternal = eventMessageServiceInternal;
        this.limitTopic = limitTopic;
    }

    @Override
    public List<TransferChannelLimitResp> getList(TransferChannelLimitFilter filter, String sort) {
        return TransferChannelLimit2TransferChannelLimitRespMapper.INSTANCE.map(transferChannelLimitRepository.findAll(Filters.toSpecification(filter), Pageables.of(0, 1,sort).getSort()));
    }

    @Override
    public TransferChannelLimitResp getDetail(Long id) {
        return TransferChannelLimit2TransferChannelLimitRespMapper.INSTANCE.map(transferChannelLimitRepository.findById(id)
                .orElseThrow(() -> new PaymentPlatformException(ErrorCode.TRANSFER_CHANNEL_LIMIT_NOT_FOUND, ErrorCode.TRANSFER_CHANNEL_LIMIT_NOT_FOUND.message())));
    }

    @Override
    public CompletableFuture<TransferChannelLimitResp> create(Authentication authentication, CreateTransferChannelLimitRequest request) {
        createTransferChannelLimitValidator.validate(request);
        var username = Authentications.requireUsername(authentication);
        var channelLimit = create0(CreateTransferChannelLimitRequest2TransferChannelLimitMapper.INSTANCE.map(request, username));

        var syncMessage = MessageHelper.toSyncMessage(OpType.I, TransferChannelLimit2TransferChannelLimitMessageMapper.INSTANCE.map(channelLimit));
        var eventMessage = eventMessageServiceInternal.save(limitTopic, channelLimit.getId().toString(),
                username, Json.encode(syncMessage, TRANSFER_CHANNEL_LIMIT_WRITER));
        return eventMessageServiceInternal.sendAndUpdate9093Async(eventMessage)
                .thenApply(result -> TransferChannelLimit2TransferChannelLimitRespMapper.INSTANCE.map(channelLimit))
                .exceptionally(throwable -> {
                    throw CompletableFutures.toCompletionException(
                            new PaymentPlatformException(ErrorCode.SEND_AND_UPDATE_EVENT_ERROR,
                                    ErrorCode.SEND_AND_UPDATE_EVENT_ERROR.message()));
                });
    }

    @Override
    public CompletableFuture<TransferChannelLimitResp> update(Authentication authentication, Long id, UpdateTransferChannelLimitRequest request) {
        var username = Authentications.requireUsername(authentication);
        var transferChannelLimit = transferChannelLimitRepository.getLockedById(id)
                .orElseThrow(() -> new PaymentPlatformException(ErrorCode.TRANSFER_CHANNEL_LIMIT_NOT_FOUND, ErrorCode.TRANSFER_CHANNEL_LIMIT_NOT_FOUND.message()));
        request.setTransferChannel(transferChannelLimit.getTransferChannel());
        updateTransferChannelLimitValidator.validate(request);
        var updateData = UpdateTransferChannelLimitReq2TransferChannelLimitMapper.INSTANCE.map(request)
                .setId(id)
                .setTransferChannel(transferChannelLimit.getTransferChannel())
                .setCreatedAt(transferChannelLimit.getCreatedAt())
                .setCreatedBy(transferChannelLimit.getCreatedBy())
                .setUpdatedBy(username)
                .setUpdatedAt(OffsetDateTime.now());

        TransferChannelLimit dataUpdate = updateReturning(updateData);

        var syncMessage = MessageHelper.toSyncMessage(OpType.U, TransferChannelLimit2TransferChannelLimitMessageMapper.INSTANCE.map(dataUpdate));
        var eventMessage = eventMessageServiceInternal.save(limitTopic, dataUpdate.getId().toString(),
                username, Json.encode(syncMessage, TRANSFER_CHANNEL_LIMIT_WRITER));
        return eventMessageServiceInternal.sendAndUpdate9093Async(eventMessage)
                .thenApply((Void) ->
                        TransferChannelLimit2TransferChannelLimitRespMapper.INSTANCE.map(dataUpdate))
                .exceptionally(throwable -> {
                    throw CompletableFutures.toCompletionException(
                            new PaymentPlatformException(ErrorCode.SEND_AND_UPDATE_EVENT_ERROR,
                                    ErrorCode.SEND_AND_UPDATE_EVENT_ERROR.message()));
                });
    }

    @Override
    public TransferChannelLimit create0(TransferChannelLimit account) {
        try {
            return transferChannelLimitRepository.persistAndFlush(account);
        } catch (Exception ex) {
            log.error("Create transfer channel limit fail", ex);
            if (ex.getCause() instanceof ConstraintViolationException cve && cve.getConstraintName() != null
                    && (cve.getConstraintName().endsWith("PAP_TRANSFER_CHANNEL_LIMIT_TRANSFER_CHANNEL_UINDEX"))) {
                throw new PaymentPlatformException(ErrorCode.TRANSFER_CHANNEL_LIMIT_EXISTED, ErrorCode.TRANSFER_CHANNEL_LIMIT_EXISTED.message());
            }
            throw new PaymentPlatformException(ErrorCode.CREATE_TRANSFER_CHANNEL_LIMIT_FAIL, ErrorCode.CREATE_TRANSFER_CHANNEL_LIMIT_FAIL.message());
        }
    }

    @Override
    public TransferChannelLimit updateReturning(TransferChannelLimit transferChannelLimit) {
        try {
            return transferChannelLimitRepository.mergeAndFlush(transferChannelLimit);
        } catch (Exception ex) {
            log.error("Update transfer channel limit fail", ex);
            throw new PaymentPlatformException(ErrorCode.UPDATE_TRANSFER_CHANNEL_LIMIT_FAIL, ErrorCode.UPDATE_TRANSFER_CHANNEL_LIMIT_FAIL.message());
        }
    }

    @Override
    public Page<TransferChannelLimitHistoryResp> getListHistory(Long transferChannelId, TransferChannelLimitHistoryFilter filter, Pageable pageable) {
        filter.setTransferChannelId(transferChannelId);
        return transferChannelLimitHistoryService.getHistorys(filter, pageable);
    }
    @Override
    public TransferChannelLimitHistoryResp getDetailHistory(Long historyId) {
        return transferChannelLimitHistoryService.getDetailHistory(historyId);
    }

}
