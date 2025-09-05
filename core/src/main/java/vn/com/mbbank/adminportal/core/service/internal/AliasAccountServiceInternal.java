package vn.com.mbbank.adminportal.core.service.internal;

import vn.com.mbbank.adminportal.core.model.entity.AliasAccount;
import vn.com.mbbank.adminportal.core.service.AliasAccountService;

public interface AliasAccountServiceInternal extends AliasAccountService {

  AliasAccount create0(AliasAccount account);

  AliasAccount updateReturning(AliasAccount account);
}
