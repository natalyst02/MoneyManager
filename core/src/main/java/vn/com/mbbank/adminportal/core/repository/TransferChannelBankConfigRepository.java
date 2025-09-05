package vn.com.mbbank.adminportal.core.repository;

import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.com.mbbank.adminportal.core.model.entity.TransferChannelBankConfig;

import java.util.Optional;

@Repository
public interface TransferChannelBankConfigRepository extends BaseJpaRepository<TransferChannelBankConfig, Long>, JpaSpecificationExecutor<TransferChannelBankConfig> {
  Optional<TransferChannelBankConfig> findByIdAndActive(Long id, boolean active);

  Optional<TransferChannelBankConfig> findById(Long id);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Transactional
  @Query("select t from TransferChannelBankConfig t where t.id = :id")
  Optional<TransferChannelBankConfig> getLockedTransferChannelBankConfig(Long id);
}
