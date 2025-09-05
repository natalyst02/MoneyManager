package vn.com.mbbank.adminportal.common.repository;

import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import vn.com.mbbank.adminportal.common.model.entity.ErrorMappingConfig;

import java.util.Optional;

public interface ErrorMappingConfigRepository extends BaseJpaRepository<ErrorMappingConfig, Long> {
  Optional<ErrorMappingConfig> findByCode(String code);
}
