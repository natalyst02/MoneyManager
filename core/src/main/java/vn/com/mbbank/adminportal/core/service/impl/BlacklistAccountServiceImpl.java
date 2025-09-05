package vn.com.mbbank.adminportal.core.service.impl;

import com.dslplatform.json.JsonWriter;
import com.dslplatform.json.runtime.Generics;
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
import vn.com.mbbank.adminportal.common.exception.PaymentPlatformException;
import vn.com.mbbank.adminportal.common.model.OpType;
import vn.com.mbbank.adminportal.common.model.SyncMessage;
import vn.com.mbbank.adminportal.common.service.internal.EventMessageServiceInternal;
import vn.com.mbbank.adminportal.common.util.CompletableFutures;
import vn.com.mbbank.adminportal.common.util.Json;
import vn.com.mbbank.adminportal.core.mapper.*;
import vn.com.mbbank.adminportal.core.model.entity.BlacklistAccount;
import vn.com.mbbank.adminportal.core.model.filter.BlacklistAccountFilter;
import vn.com.mbbank.adminportal.core.model.filter.BlacklistAccountHistoryFilter;
import vn.com.mbbank.adminportal.core.model.message.BlacklistAccountMessage;
import vn.com.mbbank.adminportal.core.model.request.CreateBlacklistAccountRequest;
import vn.com.mbbank.adminportal.core.model.request.UpdateBlacklistAccountRequest;
import vn.com.mbbank.adminportal.core.model.response.BlacklistAccountHistoryResponse;
import vn.com.mbbank.adminportal.core.model.response.BlacklistAccountResponse;
import vn.com.mbbank.adminportal.core.repository.BlacklistAccountHistoryRepository;
import vn.com.mbbank.adminportal.core.repository.BlacklistAccountRepository;
import vn.com.mbbank.adminportal.core.service.internal.BlacklistAccountServiceInternal;
import vn.com.mbbank.adminportal.core.util.Authentications;
import vn.com.mbbank.adminportal.core.util.ErrorCode;
import vn.com.mbbank.adminportal.core.util.Filters;
import vn.com.mbbank.adminportal.core.util.MessageHelper;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.OffsetDateTime;
import java.util.concurrent.CompletableFuture;

@Service
@Log4j2
@RequiredArgsConstructor
public class BlacklistAccountServiceImpl implements BlacklistAccountServiceInternal {
  @Value("${blacklist-account.topic}")
  private String blacklistAccountTopic;

  private final BlacklistAccountRepository blacklistAccountRepository;
  private final BlacklistAccountHistoryRepository blacklistAccountHistoryRepository;
  private final EventMessageServiceInternal eventMessageServiceInternal;

  private final JsonWriter.WriteObject<SyncMessage<BlacklistAccountMessage>> BLACKLIST_ACCOUNT_WRITER =
      Json.findWriter(Generics.makeParameterizedType(SyncMessage.class, BlacklistAccountMessage.class));

  @Override
  public Page<BlacklistAccountResponse> search(BlacklistAccountFilter filter, Pageable pageable) {
    return blacklistAccountRepository.findAll(Filters.toSpecification(filter), pageable).map(BlacklistAccount2BlacklistAccountResponseMapper.INSTANCE::map);
  }

  @Override
  public BlacklistAccountResponse get(Long id) {
    return blacklistAccountRepository.getBlacklistAccountById(id).map(BlacklistAccount2BlacklistAccountResponseMapper.INSTANCE::map)
        .orElseThrow(() -> new PaymentPlatformException(ErrorCode.BLACKLIST_ACCOUNT_NOT_FOUND, ErrorCode.BLACKLIST_ACCOUNT_NOT_FOUND.message()));
  }

  @Override
  public BlacklistAccount getLocked(Long id) {
    return blacklistAccountRepository
        .getLockedById(id)
        .orElseThrow(() -> new PaymentPlatformException(ErrorCode.BLACKLIST_ACCOUNT_NOT_FOUND, ErrorCode.BLACKLIST_ACCOUNT_NOT_FOUND.message()));
  }

  @Override
  public CompletableFuture<BlacklistAccountResponse> create(Authentication authentication, CreateBlacklistAccountRequest createBlacklistAccountRequest) {
    var username = Authentications.requireUsername(authentication);
    var blacklistAccount = create(CreateBlacklistAccountReq2BlacklistAccount.INSTANCE.map(createBlacklistAccountRequest, username));
    var syncMessage = MessageHelper.toSyncMessage(OpType.I, BlacklistAccount2MessageMapper.INSTANCE.map(blacklistAccount));
    var eventMessage = eventMessageServiceInternal.save(blacklistAccountTopic, blacklistAccount.getId().toString(),
        username, Json.encode(syncMessage, BLACKLIST_ACCOUNT_WRITER));
    return eventMessageServiceInternal.sendAndUpdate9093Async(eventMessage)
        .thenApply(result -> BlacklistAccount2BlacklistAccountResponseMapper.INSTANCE.map(blacklistAccount))
        .exceptionally(throwable -> {
          throw CompletableFutures.toCompletionException(
              new PaymentPlatformException(ErrorCode.SEND_AND_UPDATE_EVENT_ERROR,
                  ErrorCode.SEND_AND_UPDATE_EVENT_ERROR.message()));
        });
  }

  @Override
  public CompletableFuture<BlacklistAccountResponse> update(Authentication authentication, Long id, UpdateBlacklistAccountRequest updateBlacklistAccountRequest) {
    var username = Authentications.requireUsername(authentication);
    var blacklistAccount = getLocked(id);
    var updateData = UpdateBlacklistAccountReq2BlacklistAccount.INSTANCE.map(updateBlacklistAccountRequest)
        .setId(id)
        .setCreatedAt(blacklistAccount.getCreatedAt())
        .setCreatedBy(blacklistAccount.getCreatedBy())
        .setUpdatedBy(username)
        .setUpdatedAt(OffsetDateTime.now());

    var dataUpdate = updateReturning(updateData);

    var syncMessage = MessageHelper.toSyncMessage(OpType.U, BlacklistAccount2MessageMapper.INSTANCE.map(dataUpdate));
    var eventMessage = eventMessageServiceInternal.save(blacklistAccountTopic, dataUpdate.getId().toString(),
        username, Json.encode(syncMessage, BLACKLIST_ACCOUNT_WRITER));
    return eventMessageServiceInternal.sendAndUpdate9093Async(eventMessage)
        .thenApply((Void) ->
            BlacklistAccount2BlacklistAccountResponseMapper.INSTANCE.map(dataUpdate))
        .exceptionally(throwable -> {
          throw CompletableFutures.toCompletionException(
              new PaymentPlatformException(ErrorCode.SEND_AND_UPDATE_EVENT_ERROR,
                  ErrorCode.SEND_AND_UPDATE_EVENT_ERROR.message()));
        });
  }

  @Override
  public BlacklistAccount create(BlacklistAccount blacklistAccount) {
    try {
      return blacklistAccountRepository.persistAndFlush(blacklistAccount);
    } catch (DataIntegrityViolationException e) {
      if (e.getCause() instanceof ConstraintViolationException cve
          && cve.getConstraintName() != null) {
        if (cve.getConstraintName()
            .endsWith("PAP_BLACKLIST_ACCOUNT_TYPE_TRANSACTION_TYPE_BANK_CODE_ACCOUNT_NO_UINDEX")) {
          throw new PaymentPlatformException(
              ErrorCode.DUPLICATE_ACCOUNT_TYPE_TRANSACTION_TYPE_BANK_CODE_ACCOUNT_NO,
              "Blacklist account with type "
                  + blacklistAccount.getType()
                  + " and transactionType "
                  + blacklistAccount.getTransactionType()
                  + " and bankCode "
                  + blacklistAccount.getBankCode()
                  + " and accountNo "
                  + blacklistAccount.getAccountNo()
                  + " already exists");
        }
      }
      throw new PaymentPlatformException(ErrorCode.CREATE_BLACKLIST_ACCOUNT_FAIL, ErrorCode.CREATE_BLACKLIST_ACCOUNT_FAIL.message());
    }
  }

  @Override
  public BlacklistAccount updateReturning(BlacklistAccount blacklistAccount) {
    try {
      return blacklistAccountRepository.updateAccount(blacklistAccount)
          .orElseThrow(() -> new PaymentPlatformException(ErrorCode.UPDATE_BLACKLIST_ACCOUNT_FAIL, ErrorCode.UPDATE_BLACKLIST_ACCOUNT_FAIL.message()));
    } catch (DuplicateKeyException e) {
      if (e.getCause() instanceof SQLIntegrityConstraintViolationException cve && cve.getMessage() != null) {
        if (cve.getMessage().contains("PAP_BLACKLIST_ACCOUNT_TYPE_TRANSACTION_TYPE_BANK_CODE_ACCOUNT_NO_UINDEX")) {
          throw new PaymentPlatformException(
              ErrorCode.DUPLICATE_ACCOUNT_TYPE_TRANSACTION_TYPE_BANK_CODE_ACCOUNT_NO,
              "Blacklist account with type "
                  + blacklistAccount.getType()
                  + " and transactionType "
                  + blacklistAccount.getTransactionType()
                  + " and bankCode "
                  + blacklistAccount.getBankCode()
                  + " and accountNo "
                  + blacklistAccount.getAccountNo()
                  + " already exists");
        }
      }
      throw new PaymentPlatformException(ErrorCode.UPDATE_BLACKLIST_ACCOUNT_FAIL, ErrorCode.UPDATE_BLACKLIST_ACCOUNT_FAIL.message());
    }
  }

  @Override
  public Page<BlacklistAccountHistoryResponse> searchHistory(Long id, BlacklistAccountHistoryFilter filter, Pageable pageable) {
    filter.setBlacklistAccountId(id);
    return blacklistAccountHistoryRepository.findAll(Filters.toSpecification(filter), pageable)
        .map(BlacklistAccountHistory2BlacklistAccountHistoryResponseMapper.INSTANCE::map);
  }

  @Override
  public BlacklistAccountHistoryResponse getHistory(Long id) {
    return blacklistAccountHistoryRepository.getBlacklistAccountHistoryById(id).map(BlacklistAccountHistory2BlacklistAccountHistoryResponseMapper.INSTANCE::map)
        .orElseThrow(() -> new PaymentPlatformException(ErrorCode.BLACKLIST_ACCOUNT_HISTORY_NOT_FOUND, ErrorCode.BLACKLIST_ACCOUNT_HISTORY_NOT_FOUND.message()));
  }
}
