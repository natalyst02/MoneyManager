package vn.com.mbbank.adminportal.core.repository;

import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.com.mbbank.adminportal.core.model.entity.WhitelistAccountHistory;

import java.util.Optional;

public interface WhitelistAccountHistoryRepository extends BaseJpaRepository<WhitelistAccountHistory, Long>, CustomizedWhitelistAccountRepository, JpaSpecificationExecutor<WhitelistAccountHistory> {
  Optional<WhitelistAccountHistory> getWhitelistAccountHistoryById(Long id);
}
