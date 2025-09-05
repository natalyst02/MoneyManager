package vn.com.mbbank.adminportal.core.repository;

import vn.com.mbbank.adminportal.core.model.entity.AliasAccount;

import java.util.Optional;

public interface CustomizedAliasAccountRepository {
  Optional<AliasAccount> updateAliasAccount(AliasAccount account);
}
