package vn.com.mbbank.adminportal.core.repository;

import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.transaction.annotation.Transactional;
import vn.com.mbbank.adminportal.core.model.entity.WhitelistAccount;

import java.util.Optional;

public interface WhitelistAccountRepository extends BaseJpaRepository<WhitelistAccount, Long>, CustomizedWhitelistAccountRepository, JpaSpecificationExecutor<WhitelistAccount> {
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Transactional
  Optional<WhitelistAccount> getLockedById(Long id);
  Optional<WhitelistAccount> getWhitelistAccountById(Long id);
}
