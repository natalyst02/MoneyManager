package vn.com.mbbank.adminportal.core.service.internal;

import vn.com.mbbank.adminportal.core.model.entity.WhitelistAccount;
import vn.com.mbbank.adminportal.core.service.WhitelistAccountService;

public interface WhitelistAccountServiceInternal extends WhitelistAccountService {
  WhitelistAccount get0(Long id);

  WhitelistAccount create(WhitelistAccount whitelistAccount);

  WhitelistAccount update(WhitelistAccount whitelistAccount);
}
