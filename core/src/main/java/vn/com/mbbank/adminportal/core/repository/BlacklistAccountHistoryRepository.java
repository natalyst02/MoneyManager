package vn.com.mbbank.adminportal.core.repository;

import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.com.mbbank.adminportal.core.model.entity.BlacklistAccountHistory;

import java.util.Optional;

public interface BlacklistAccountHistoryRepository extends BaseJpaRepository<BlacklistAccountHistory, Long>, JpaSpecificationExecutor<BlacklistAccountHistory> {
  Optional<BlacklistAccountHistory> getBlacklistAccountHistoryById(Long id);
}
