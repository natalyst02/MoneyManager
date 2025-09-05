package vn.com.mbbank.adminportal.core.repository;

import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.com.mbbank.adminportal.core.model.entity.AliasAccountHistory;

@Repository
public interface AliasAccountHistoryRepository extends BaseJpaRepository<AliasAccountHistory, Long>, JpaSpecificationExecutor<AliasAccountHistory> {
}
