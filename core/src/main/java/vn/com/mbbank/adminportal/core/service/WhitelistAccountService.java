package vn.com.mbbank.adminportal.core.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import vn.com.mbbank.adminportal.core.model.filter.WhitelistAccountFilter;
import vn.com.mbbank.adminportal.core.model.filter.WhitelistAccountHistoryFilter;
import vn.com.mbbank.adminportal.core.model.request.CreateWhitelistAccountRequest;
import vn.com.mbbank.adminportal.core.model.request.RejectWhitelistAccountRequest;
import vn.com.mbbank.adminportal.core.model.request.UpdateWhitelistAccountRequest;
import vn.com.mbbank.adminportal.core.model.response.WhitelistAccountHistoryResponse;
import vn.com.mbbank.adminportal.core.model.response.WhitelistAccountResponse;

public interface WhitelistAccountService {
  WhitelistAccountResponse create(Authentication authentication, CreateWhitelistAccountRequest createWhitelistAccountRequest);

  WhitelistAccountResponse update(Authentication authentication, Long id, UpdateWhitelistAccountRequest updateWhitelistAccountRequest);

  WhitelistAccountResponse reject(Authentication authentication, Long id, RejectWhitelistAccountRequest rejectWhitelistAccountRequest);
  WhitelistAccountResponse approve(Authentication authentication, Long id);
  Page<WhitelistAccountResponse> getWhitelistAccounts(Authentication authentication, WhitelistAccountFilter filter, Pageable pageable);
  WhitelistAccountResponse get(Long id);
  Page<WhitelistAccountHistoryResponse> searchHistory(Long id, Authentication authentication, WhitelistAccountHistoryFilter filter, Pageable pageable);
  WhitelistAccountHistoryResponse getHistory(Long id);
}
