package vn.com.mbbank.adminportal.core.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import vn.com.mbbank.adminportal.core.model.filter.AliasAccountFilter;
import vn.com.mbbank.adminportal.core.model.filter.AliasAccountHistoryFilter;
import vn.com.mbbank.adminportal.core.model.request.CreateAliasAccountRequest;
import vn.com.mbbank.adminportal.core.model.request.RejectAliasAccountRequest;
import vn.com.mbbank.adminportal.core.model.request.UpdateAliasAccountRequest;
import vn.com.mbbank.adminportal.core.model.response.*;

public interface AliasAccountService {
  CreateAliasAccountResp create(Authentication authentication, CreateAliasAccountRequest request);

  UpdateAliasAccountResp update(Authentication authentication, Long id, UpdateAliasAccountRequest request);

  RejectAliasAccountResp reject(Authentication authentication, Long id, RejectAliasAccountRequest request);

  ApproveAliasAccountResp approve(Authentication authentication, Long id);

  Page<AliasAccountResp> getAccounts(AliasAccountFilter filter, Pageable pageable);

  AliasAccountResp getAccount(Long id);

  Page<AliasAccountHistoryResp> getAccountHistory(Long name, AliasAccountHistoryFilter filter, Pageable pageable);

  AliasAccountHistoryResp getAccountHistory(Long id);
}
