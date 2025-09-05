package vn.com.mbbank.adminportal.core.repository;

import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vn.com.mbbank.adminportal.core.model.entity.TransferChannelLimit;

import java.util.Optional;

@Repository
public interface TransferChannelLimitRepository extends BaseJpaRepository<TransferChannelLimit, Long>, JpaSpecificationExecutor<TransferChannelLimit> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Transactional
    Optional<TransferChannelLimit> getLockedById(Long id);
}
