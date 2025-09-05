package vn.com.mbbank.adminportal.core.repository;

import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.com.mbbank.adminportal.core.model.entity.Bank;

@Repository
public interface BankRepository extends BaseJpaRepository<Bank, Long>, JpaSpecificationExecutor<Bank> {
}
