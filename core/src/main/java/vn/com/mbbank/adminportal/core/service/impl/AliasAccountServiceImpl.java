package vn.com.mbbank.adminportal.core.service.impl;

import com.dslplatform.json.JsonWriter;
import com.dslplatform.json.runtime.Generics;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import vn.com.mbbank.adminportal.common.exception.NSTCompletionException;
import vn.com.mbbank.adminportal.common.exception.PaymentPlatformException;
import vn.com.mbbank.adminportal.common.model.OpType;
import vn.com.mbbank.adminportal.common.model.SyncMessage;
import vn.com.mbbank.adminportal.common.model.entity.EventMessage;
import vn.com.mbbank.adminportal.common.service.internal.EventMessageServiceInternal;
import vn.com.mbbank.adminportal.common.util.DataHolder;
import vn.com.mbbank.adminportal.common.util.Json;
import vn.com.mbbank.adminportal.common.util.RestApiHelper;
import vn.com.mbbank.adminportal.core.mapper.*;
import vn.com.mbbank.adminportal.core.mapper.AliasAccount2AliasAccountRespMapper;
import vn.com.mbbank.adminportal.core.mapper.AliasAccount2CreateAliasAccountRespMapper;
import vn.com.mbbank.adminportal.core.model.ApprovalStatus;
import vn.com.mbbank.adminportal.core.model.filter.AliasAccountHistoryFilter;
import vn.com.mbbank.adminportal.core.model.message.AliasAccountMessage;
import vn.com.mbbank.adminportal.core.model.filter.AliasAccountFilter;
import vn.com.mbbank.adminportal.core.model.request.CreateAliasAccountRequest;
import vn.com.mbbank.adminportal.core.model.request.RejectAliasAccountRequest;
import vn.com.mbbank.adminportal.core.model.request.UpdateAliasAccountRequest;
import vn.com.mbbank.adminportal.core.model.response.*;
import vn.com.mbbank.adminportal.core.model.entity.AliasAccount;
import vn.com.mbbank.adminportal.core.repository.AliasAccountHistoryRepository;
import vn.com.mbbank.adminportal.core.repository.AliasAccountRepository;
import vn.com.mbbank.adminportal.core.service.internal.AliasAccountServiceInternal;
import vn.com.mbbank.adminportal.core.util.Authentications;
import vn.com.mbbank.adminportal.core.util.ErrorCode;
import vn.com.mbbank.adminportal.core.util.Filters;
import vn.com.mbbank.adminportal.core.validator.CreateAliasAccountValidator;
import vn.com.mbbank.adminportal.core.validator.UpdateAliasAccountValidator;

import java.time.OffsetDateTime;


@Service
@RequiredArgsConstructor
@Log4j2
public class AliasAccountServiceImpl implements AliasAccountServiceInternal {
  @Value("${alias.account.topic}")
  private String aliasTopic;
  private final AliasAccountRepository aliasAccountRepository;
  private final CreateAliasAccountValidator createAliasAccountValidator;
  private final UpdateAliasAccountValidator updateAliasAccountValidator;
  private final EventMessageServiceInternal eventMessageServiceInternal;
  private final TransactionTemplate transactionTemplate;
  private final AliasAccountHistoryRepository aliasAccountHistoryRepository;
  private final JsonWriter.WriteObject<SyncMessage<AliasAccountMessage>> ALIAS_ACCOUNT_WRITER = Json.findWriter(Generics.makeParameterizedType(SyncMessage.class, AliasAccountMessage.class));

  @Override
  public CreateAliasAccountResp create(Authentication authentication, CreateAliasAccountRequest request) {
    createAliasAccountValidator.validate(request);
    var username = Authentications.requireUsername(authentication);
    try {
      var aliasAccount = create0(CreateAliasAcctRequest2AliasAcctMapper.INSTANCE.map(request, username));
      return AliasAccount2CreateAliasAccountRespMapper.INSTANCE.map(aliasAccount);
    } catch (Exception ex) {
      log.error("Create alias account fail", ex);
      if (ex.getCause() instanceof ConstraintViolationException cve && cve.getConstraintName() != null
          && (cve.getConstraintName().endsWith("PAP_ALIAS_ACCOUNT_NAME_INDEX"))) {
        throw new NSTCompletionException(new PaymentPlatformException(ErrorCode.ALIAS_ACCOUNT_NAME_EXISTED, ErrorCode.ALIAS_ACCOUNT_NAME_EXISTED.message()));
      }
      throw new NSTCompletionException(new PaymentPlatformException(ErrorCode.CREATE_ALIAS_ACCOUNT_FAIL, ErrorCode.CREATE_ALIAS_ACCOUNT_FAIL.message()));
    }
  }

  @Override
  @Transactional
  public UpdateAliasAccountResp update(Authentication authentication, Long id, UpdateAliasAccountRequest request) {
    updateAliasAccountValidator.validate(request);
    var username = Authentications.requireUsername(authentication);
    var aliasAccount = aliasAccountRepository.getLockedById(id)
        .orElseThrow(() -> new PaymentPlatformException(ErrorCode.ALIAS_ACCOUNT_NOT_FOUND, ErrorCode.ALIAS_ACCOUNT_NOT_FOUND.message()));
    if (!aliasAccount.getApprovalStatus().isAllowedChange(ApprovalStatus.WAITING_APPROVAL)) {
      throw new NSTCompletionException(new PaymentPlatformException(ErrorCode.ALIAS_ACCOUNT_CANT_BE_UPDATED, ErrorCode.ALIAS_ACCOUNT_CANT_BE_UPDATED.message()));
    }
    var updateAccount = UpdateAliasAcctReq2AliasAccountMapper.INSTANCE.map(request)
        .setId(id)
        .setName(aliasAccount.getName())
        .setApprovalStatus(ApprovalStatus.WAITING_APPROVAL)
        .setApprovedBy(aliasAccount.getApprovedBy())
        .setApprovedAt(aliasAccount.getApprovedAt())
        .setUpdatedBy(username)
        .setUpdatedAt(OffsetDateTime.now());
    return AliasAccount2UpdateAliasAcctRespMapper.INSTANCE.map(updateReturning(updateAccount));
  }

  @Override
  @Transactional
  public RejectAliasAccountResp reject(Authentication authentication, Long id, RejectAliasAccountRequest request) {
    var username = Authentications.requireUsername(authentication);
    var aliasAccount = aliasAccountRepository.getLockedById(id)
        .orElseThrow(() -> new NSTCompletionException(new PaymentPlatformException(ErrorCode.ALIAS_ACCOUNT_NOT_FOUND, ErrorCode.ALIAS_ACCOUNT_NOT_FOUND.message())));
    if (!aliasAccount.getApprovalStatus().isAllowedChange(ApprovalStatus.REJECTED)) {
      throw new NSTCompletionException(new PaymentPlatformException(ErrorCode.ALIAS_ACCOUNT_CANT_BE_UPDATED, ErrorCode.ALIAS_ACCOUNT_CANT_BE_UPDATED.message()));
    }
    var copiedAccount = aliasAccount.shallowCopy()
        .setReason(request.getReason())
        .setApprovalStatus(ApprovalStatus.REJECTED)
        .setUpdatedBy(username)
        .setUpdatedAt(OffsetDateTime.now());
    return AliasAccount2RejectAliasAcctRespMapper.INSTANCE.map(updateReturning(copiedAccount));
  }

  @Override
  public ApproveAliasAccountResp approve(Authentication authentication, Long id) {
    var username = Authentications.requireUsername(authentication);
    var eventMessageDataHolder = new DataHolder<EventMessage>();
    var approveResult = transactionTemplate.execute(status -> {
      try {
        var aliasAccount = aliasAccountRepository.getLockedById(id)
            .orElseThrow(() -> new NSTCompletionException(new PaymentPlatformException(ErrorCode.ALIAS_ACCOUNT_NOT_FOUND, ErrorCode.ALIAS_ACCOUNT_NOT_FOUND.message())));
        if (!aliasAccount.getApprovalStatus().isAllowedChange(ApprovalStatus.APPROVED)) {
          throw new NSTCompletionException(new PaymentPlatformException(ErrorCode.ALIAS_ACCOUNT_CANT_BE_UPDATED, ErrorCode.ALIAS_ACCOUNT_CANT_BE_UPDATED.message()));
        }
        var action = aliasAccount.getApprovedBy() != null ? OpType.U : OpType.I;
        var now = OffsetDateTime.now();
        var copiedAccount = aliasAccount.shallowCopy();
        var approvedAccount = updateReturning(copiedAccount.setApprovalStatus(ApprovalStatus.APPROVED)
            .setApprovedBy(username)
            .setApprovedAt(now)
            .setUpdatedBy(username)
            .setUpdatedAt(now));

        var syncMessage = new SyncMessage<AliasAccountMessage>()
            .setData(AliasAcct2AliasAcctMessageMapper.INSTANCE.map(approvedAccount))
            .setAction(action)
            .setIat(now)
            .setClientMessageId(RestApiHelper.getOrCreateClientMessageId());
        var syncedMessage = eventMessageServiceInternal.sendAsync(aliasTopic, String.valueOf(id), username, syncMessage, ALIAS_ACCOUNT_WRITER).join();
        eventMessageDataHolder.setValue(syncedMessage);
        return approvedAccount;
      } catch (Exception ex) {
        log.error("Approve alias account fail", ex);
        status.isRollbackOnly();
        if (ex instanceof NSTCompletionException) throw ex;
        throw new NSTCompletionException(new PaymentPlatformException(ErrorCode.APPROVE_ALIAS_ACCOUNT_FAIL, ErrorCode.APPROVE_ALIAS_ACCOUNT_FAIL.message()));
      }
    });
    eventMessageServiceInternal.markSent(eventMessageDataHolder.getValue().getId(), username, OffsetDateTime.now());
    return AliasAccount2ApproveAliasAccountRespMapper.INSTANCE.map(approveResult);
  }

  @Override
  public Page<AliasAccountResp> getAccounts(AliasAccountFilter filter, Pageable pageable) {
    return aliasAccountRepository.findAll(Filters.toSpecification(filter), pageable).map(AliasAccount2AliasAccountRespMapper.INSTANCE::map);
  }

  @Override
  public AliasAccountResp getAccount(Long id) {
    return AliasAccount2AliasAccountRespMapper.INSTANCE.map(aliasAccountRepository.findById(id)
        .orElseThrow(() -> new PaymentPlatformException(ErrorCode.ALIAS_ACCOUNT_NOT_FOUND, ErrorCode.ALIAS_ACCOUNT_NOT_FOUND.message())));
  }

  @Override
  public AliasAccount create0(AliasAccount account) {
    return aliasAccountRepository.persistAndFlush(account);
  }

  @Override
  public AliasAccount updateReturning(AliasAccount account) {
    return aliasAccountRepository.updateAliasAccount(account)
        .orElseThrow(() -> new PaymentPlatformException(ErrorCode.ALIAS_ACCOUNT_NOT_FOUND, ErrorCode.ALIAS_ACCOUNT_NOT_FOUND.message()));
  }

  @Override
  public Page<AliasAccountHistoryResp> getAccountHistory(Long papAliasAccountId, AliasAccountHistoryFilter filter, Pageable pageable) {
    var aliasFilter = filter.setAliasAccountId(papAliasAccountId);
    return aliasAccountHistoryRepository.findAll(Filters.toSpecification(aliasFilter), pageable).map(AliasAccountHistory2AliasAccountHistoryRespMapper.INSTANCE::map);
  }

  @Override
  public AliasAccountHistoryResp getAccountHistory(Long historyId) {
    return AliasAccountHistory2AliasAccountHistoryRespMapper.INSTANCE.map(aliasAccountHistoryRepository.findById(historyId)
        .orElseThrow(() -> new PaymentPlatformException(ErrorCode.ALIAS_ACCOUNT_HISTORY_NOT_FOUND, ErrorCode.ALIAS_ACCOUNT_HISTORY_NOT_FOUND.message())));
  }
}
