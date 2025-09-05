package vn.com.mbbank.adminportal.core.repository;

import vn.com.mbbank.adminportal.core.model.entity.BlacklistAccount;

import java.util.Optional;

public interface CustomizedBlacklistAccountRepository {
  Optional<BlacklistAccount> updateAccount(BlacklistAccount blacklistAccount);
}
