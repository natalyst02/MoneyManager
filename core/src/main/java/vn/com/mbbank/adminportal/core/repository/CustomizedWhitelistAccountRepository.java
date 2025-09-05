package vn.com.mbbank.adminportal.core.repository;

import vn.com.mbbank.adminportal.core.model.entity.WhitelistAccount;

import java.util.Optional;

public interface CustomizedWhitelistAccountRepository {
  Optional<WhitelistAccount> updateAccount(WhitelistAccount whitelistAccount);
}
