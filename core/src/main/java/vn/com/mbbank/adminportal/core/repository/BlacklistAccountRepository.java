package vn.com.mbbank.adminportal.core.repository;

import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.transaction.annotation.Transactional;
import vn.com.mbbank.adminportal.core.model.entity.BlacklistAccount;

import java.util.Optional;

public interface BlacklistAccountRepository extends BaseJpaRepository<BlacklistAccount, Long>, CustomizedBlacklistAccountRepository, JpaSpecificationExecutor<BlacklistAccount> {
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Transactional
  Optional<BlacklistAccount> getLockedById(Long id);

  Optional<BlacklistAccount> getBlacklistAccountById(Long id);
}
