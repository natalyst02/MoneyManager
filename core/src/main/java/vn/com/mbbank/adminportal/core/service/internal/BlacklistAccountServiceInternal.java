package vn.com.mbbank.adminportal.core.service.internal;

import vn.com.mbbank.adminportal.core.model.entity.BlacklistAccount;
import vn.com.mbbank.adminportal.core.service.BlacklistAccountService;

public interface BlacklistAccountServiceInternal extends BlacklistAccountService {
  BlacklistAccount getLocked(Long id);

  BlacklistAccount create(BlacklistAccount blacklistAccount);

  BlacklistAccount updateReturning(BlacklistAccount blacklistAccount);
}
