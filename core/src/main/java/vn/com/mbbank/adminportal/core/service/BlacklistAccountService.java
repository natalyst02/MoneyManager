package vn.com.mbbank.adminportal.core.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import vn.com.mbbank.adminportal.core.model.filter.BlacklistAccountFilter;
import vn.com.mbbank.adminportal.core.model.filter.BlacklistAccountHistoryFilter;
import vn.com.mbbank.adminportal.core.model.request.CreateBlacklistAccountRequest;
import vn.com.mbbank.adminportal.core.model.request.UpdateBlacklistAccountRequest;
import vn.com.mbbank.adminportal.core.model.response.BlacklistAccountHistoryResponse;
import vn.com.mbbank.adminportal.core.model.response.BlacklistAccountResponse;

import java.util.concurrent.CompletableFuture;

public interface BlacklistAccountService {
  Page<BlacklistAccountResponse> search(BlacklistAccountFilter filter, Pageable pageable);

  BlacklistAccountResponse get(Long id);

  CompletableFuture<BlacklistAccountResponse> create(Authentication authentication, CreateBlacklistAccountRequest createBlacklistAccountRequest);

  CompletableFuture<BlacklistAccountResponse> update(Authentication authentication, Long id, UpdateBlacklistAccountRequest updateBlacklistAccountRequest);

  Page<BlacklistAccountHistoryResponse> searchHistory(Long id, BlacklistAccountHistoryFilter filter, Pageable pageable);

  BlacklistAccountHistoryResponse getHistory(Long id);
}
