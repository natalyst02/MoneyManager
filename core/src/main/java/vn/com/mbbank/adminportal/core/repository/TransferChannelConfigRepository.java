package vn.com.mbbank.adminportal.core.repository;

import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import vn.com.mbbank.adminportal.core.model.TransferChannelReference;
import vn.com.mbbank.adminportal.core.model.entity.TransferChannelConfig;

import java.util.List;
import java.util.Optional;

public interface TransferChannelConfigRepository extends BaseJpaRepository<TransferChannelConfig, Long>, CustomizedTransferChannelConfigRepository, JpaSpecificationExecutor<TransferChannelConfig> {
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Transactional
  @Query("select t from TransferChannelConfig t where t.id = :id")
  Optional<TransferChannelConfig> getLocked(Long id);

  @Query("select t.id as id, t.transferChannel as transferChannel, t.priority as priority from TransferChannelConfig t")
  List<TransferChannelReference> getAll();
}
