package vn.com.mbbank.adminportal.core.service.impl;

import com.dslplatform.json.JsonWriter;
import com.dslplatform.json.runtime.Generics;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
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
import vn.com.mbbank.adminportal.core.model.ApprovalStatus;
import vn.com.mbbank.adminportal.core.model.entity.WhitelistAccount;
import vn.com.mbbank.adminportal.core.model.filter.WhitelistAccountFilter;
import vn.com.mbbank.adminportal.core.model.filter.WhitelistAccountHistoryFilter;
import vn.com.mbbank.adminportal.core.model.message.WhitelistAccountMessage;
import vn.com.mbbank.adminportal.core.model.request.CreateWhitelistAccountRequest;
import vn.com.mbbank.adminportal.core.model.request.RejectWhitelistAccountRequest;
import vn.com.mbbank.adminportal.core.model.request.UpdateWhitelistAccountRequest;
import vn.com.mbbank.adminportal.core.model.response.WhitelistAccountHistoryResponse;
import vn.com.mbbank.adminportal.core.model.response.WhitelistAccountResponse;
import vn.com.mbbank.adminportal.core.repository.WhitelistAccountHistoryRepository;
import vn.com.mbbank.adminportal.core.repository.WhitelistAccountRepository;
import vn.com.mbbank.adminportal.core.service.internal.WhitelistAccountServiceInternal;
import vn.com.mbbank.adminportal.core.util.Authentications;
import vn.com.mbbank.adminportal.core.util.ErrorCode;
import vn.com.mbbank.adminportal.core.util.Filters;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;

@Service
@Log4j2
@RequiredArgsConstructor
public class WhitelistAccountServiceImpl implements WhitelistAccountServiceInternal {
  @Value("${whitelist-account.topic}")
  private String whitelistAccountTopic;
  private final WhitelistAccountRepository whitelistAccountRepository;
  private final WhitelistAccountHistoryRepository whitelistAccountHistoryRepository;
  private final EventMessageServiceInternal eventMessageServiceInternal;
  private final TransactionTemplate transactionTemplate;
  private final JsonWriter.WriteObject<SyncMessage<WhitelistAccountMessage>> WHITELIST_ACCOUNT_WRITER =
      Json.findWriter(Generics.makeParameterizedType(SyncMessage.class, WhitelistAccountMessage.class));

  @Override
  public WhitelistAccount get0(Long id) {
    return whitelistAccountRepository.getLockedById(id).orElseThrow(() -> new PaymentPlatformException(ErrorCode.WHITELIST_ACCOUNT_NOT_FOUND, "Whitelist account with id " + id + " not found"));
  }

  @Override
  public WhitelistAccount create(WhitelistAccount whitelistAccount) {
    try {
      return whitelistAccountRepository.persistAndFlush(whitelistAccount);
    } catch (DataIntegrityViolationException e) {
      if (e.getCause() instanceof ConstraintViolationException cve && cve.getConstraintName() != null) {
        if (cve.getConstraintName().endsWith("PAP_WHITELIST_ACCOUNT_ACCOUNT_NO_BANK_CODE_TRANSFER_CHANNEL_UINDEX")) {
          throw new PaymentPlatformException(ErrorCode.DUPLICATE_ACCOUNT_NO_BANK_CODE_TRANSFER_CHANNEL, "Whitelist account with accountNo " + whitelistAccount.getAccountNo() + " and bankCode " + whitelistAccount.getBankCode() + " and transfer channel " + whitelistAccount.getTransferChannel() + " already exists");
        } else if (cve.getConstraintName().endsWith("PAP_WHITELIST_ACCOUNT_ACCOUNT_NO_BANK_CODE_ACTIVE_UINDEX")) {
          throw new PaymentPlatformException(ErrorCode.DUPLICATE_ACCOUNT_NO_BANK_CODE_ACTIVE, "Whitelist account with accountNo " + whitelistAccount.getAccountNo() + " and bankCode " + whitelistAccount.getBankCode() + " is active");
        }
      }
      throw e;
    }
  }

  @Override
  public WhitelistAccount update(WhitelistAccount whitelistAccount) {
    try {
      return whitelistAccountRepository.updateAccount(whitelistAccount).orElseThrow(() -> new PaymentPlatformException(ErrorCode.WHITELIST_ACCOUNT_NOT_FOUND, "Whitelist account with id " + whitelistAccount.getId() + " not found"));
    } catch (DuplicateKeyException e) {
      if (e.getCause() instanceof SQLIntegrityConstraintViolationException cve && cve.getMessage() != null) {
        if (cve.getMessage().contains("PAP_WHITELIST_ACCOUNT_ACCOUNT_NO_BANK_CODE_TRANSFER_CHANNEL_UINDEX")) {
          throw new PaymentPlatformException(ErrorCode.DUPLICATE_ACCOUNT_NO_BANK_CODE_TRANSFER_CHANNEL, "Whitelist account with accountNo " + whitelistAccount.getAccountNo() + " and bankCode " + whitelistAccount.getBankCode() + " and transfer channel " + whitelistAccount.getTransferChannel() + " already exists");
        } else if (cve.getMessage().contains("PAP_WHITELIST_ACCOUNT_ACCOUNT_NO_BANK_CODE_ACTIVE_UINDEX")) {
          throw new PaymentPlatformException(ErrorCode.DUPLICATE_ACCOUNT_NO_BANK_CODE_ACTIVE, "Whitelist account with accountNo " + whitelistAccount.getAccountNo() + " and bankCode " + whitelistAccount.getBankCode() + " is active");
        }
      }
      throw e;
    }
  }

  @Override
  public WhitelistAccountResponse create(
      Authentication authentication, CreateWhitelistAccountRequest createWhitelistAccountRequest) {

    var username = Authentications.requirePapUser().getUsername();
    var whitelistAccount = create(CreateWhitelistAccountReq2WhitelistAccount.INSTANCE.map(createWhitelistAccountRequest, username));

    return WhitelistAccount2CreateWhitelistAccountResp.INSTANCE.map(whitelistAccount);
  }

  @Override
  @Transactional
  public WhitelistAccountResponse update(Authentication authentication, Long id, UpdateWhitelistAccountRequest updateWhitelistAccountRequest) {

    var whitelistAccount = get0(id);

    if (whitelistAccount.getApprovalStatus() == ApprovalStatus.WAITING_APPROVAL) {
      throw new PaymentPlatformException(ErrorCode.INVALID_APPROVAL_STATUS, "Whitelist account with id " + whitelistAccount.getId() + " has invalid approval status " + whitelistAccount.getApprovalStatus());
    }

    var username = Authentications.requirePapUser().getUsername();
    var copiedWhitelistAccount = whitelistAccount.shallowCopy();
    var updatedWhitelistAccount = update(UpdateWhitelistAccountReq2WhitelistAccount.INSTANCE.map(updateWhitelistAccountRequest, copiedWhitelistAccount, username));

    return WhitelistAccount2UpdateWhitelistAccountResp.INSTANCE.map(updatedWhitelistAccount);
  }

  @Override
  @Transactional
  public WhitelistAccountResponse reject(Authentication authentication, Long id, RejectWhitelistAccountRequest rejectWhitelistAccountRequest) {

    var whitelistAccount = get0(id);

    if (whitelistAccount.getApprovalStatus() != ApprovalStatus.WAITING_APPROVAL) {
      throw new PaymentPlatformException(ErrorCode.INVALID_APPROVAL_STATUS, "Whitelist account with id " + whitelistAccount.getId() + " has invalid approval status " + whitelistAccount.getApprovalStatus());
    }

    var username = Authentications.requirePapUser().getUsername();
    var copiedWhitelistAccount = whitelistAccount.shallowCopy();
    var rejectedWhitelistAccount = update(RejectWhitelistAccountReq2WhitelistAccount.INSTANCE.map(rejectWhitelistAccountRequest, copiedWhitelistAccount, username));

    return WhitelistAccount2UpdateWhitelistAccountResp.INSTANCE.map(rejectedWhitelistAccount);
  }

  @Override
  public WhitelistAccountResponse approve(Authentication authentication, Long id) {
    var username = Authentications.requireUsername(authentication);
    var eventMessageDataHolder = new DataHolder<EventMessage>();
    var approveResult = transactionTemplate.execute(status -> {
      try {
        var whitelistAccount = whitelistAccountRepository.getLockedById(id)
            .orElseThrow(() -> new NSTCompletionException(new PaymentPlatformException(ErrorCode.WHITELIST_ACCOUNT_NOT_FOUND, ErrorCode.WHITELIST_ACCOUNT_NOT_FOUND.message())));
        if (!whitelistAccount.getApprovalStatus().isAllowedChange(ApprovalStatus.APPROVED)) {
          throw new NSTCompletionException(new PaymentPlatformException(ErrorCode.INVALID_APPROVAL_STATUS, ErrorCode.INVALID_APPROVAL_STATUS.message()));
        }
        var action = whitelistAccount.getApprovedBy() != null ? OpType.U : OpType.I;
        var now = OffsetDateTime.now();
        var copiedAccount = whitelistAccount.shallowCopy();
        var approvedWhitelistAccount = update(copiedAccount.setApprovalStatus(ApprovalStatus.APPROVED)
            .setApprovedBy(username)
            .setApprovedAt(now)
            .setUpdatedBy(username)
            .setUpdatedAt(now));

        var whitelistAccountMessage = new SyncMessage<WhitelistAccountMessage>()
            .setClientMessageId(RestApiHelper.getOrCreateClientMessageId())
            .setIat(now)
            .setAction(action)
            .setData(WhitelistAccount2MessageMapper.INSTANCE.map(approvedWhitelistAccount));
        var syncedMessage = eventMessageServiceInternal.sendAsync(whitelistAccountTopic, String.valueOf(id),
            username, whitelistAccountMessage, WHITELIST_ACCOUNT_WRITER).join();
        eventMessageDataHolder.setValue(syncedMessage);
        return approvedWhitelistAccount;
      } catch (Exception ex) {
        log.error("Approve whitelist account fail", ex);
        status.isRollbackOnly();
        if (ex instanceof NSTCompletionException) throw ex;
        throw new NSTCompletionException(new PaymentPlatformException(ErrorCode.APPROVE_WHITELIST_ACCOUNT_FAIL, ErrorCode.APPROVE_WHITELIST_ACCOUNT_FAIL.message()));
      }
    });
    eventMessageServiceInternal.markSent(eventMessageDataHolder.getValue().getId(), username, OffsetDateTime.now());
    return WhitelistAccount2WhitelistAccountResponseMapper.INSTANCE.map(approveResult);
  }

  @Override
  public Page<WhitelistAccountResponse> getWhitelistAccounts(Authentication authentication, WhitelistAccountFilter filter, Pageable pageable) {
    return whitelistAccountRepository.findAll(Filters.toSpecification(filter), pageable).map(WhitelistAccount2WhitelistAccountResponseMapper.INSTANCE::map);
  }

  @Override
  public WhitelistAccountResponse get(Long id) {
    return whitelistAccountRepository.getWhitelistAccountById(id).map(WhitelistAccount2WhitelistAccountResponseMapper.INSTANCE::map)
        .orElseThrow(() -> new PaymentPlatformException(ErrorCode.WHITELIST_ACCOUNT_NOT_FOUND, ErrorCode.WHITELIST_ACCOUNT_NOT_FOUND.message()));
  }

  @Override
  public Page<WhitelistAccountHistoryResponse> searchHistory(Long id, Authentication authentication, WhitelistAccountHistoryFilter filter, Pageable pageable) {
    filter.setWhitelistAccountId(id);
    return whitelistAccountHistoryRepository.findAll(Filters.toSpecification(filter), pageable)
        .map(WhitelistAccountHistory2WhitelistAccountHistoryResponseMapper.INSTANCE::map);
  }

  @Override
  public WhitelistAccountHistoryResponse getHistory(Long id) {
    return whitelistAccountHistoryRepository.getWhitelistAccountHistoryById(id).map(WhitelistAccountHistory2WhitelistAccountHistoryResponseMapper.INSTANCE::map)
        .orElseThrow(() -> new PaymentPlatformException(ErrorCode.WHITELIST_ACCOUNT_HISTORY_NOT_FOUND, ErrorCode.WHITELIST_ACCOUNT_HISTORY_NOT_FOUND.message()));
  }
}
