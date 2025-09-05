package vn.com.mbbank.adminportal.core.repository;

import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vn.com.mbbank.adminportal.core.model.entity.AliasAccount;

import java.util.Optional;

@Repository
public interface AliasAccountRepository extends BaseJpaRepository<AliasAccount, Long>, JpaSpecificationExecutor<AliasAccount>, CustomizedAliasAccountRepository {
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Transactional
  Optional<AliasAccount> getLockedById(Long id);
}
