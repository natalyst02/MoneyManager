package vn.com.mbbank.adminportal.common.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.com.mbbank.adminportal.common.config.RedisClusterAdapter;
import vn.com.mbbank.adminportal.common.model.entity.ErrorMappingConfig;
import vn.com.mbbank.adminportal.common.repository.ErrorMappingConfigRepository;
import vn.com.mbbank.adminportal.common.service.internal.ErrorMappingConfigServiceInternal;

@Service
@RequiredArgsConstructor
public class ErrorMappingConfigServiceImpl implements ErrorMappingConfigServiceInternal {
  private final ErrorMappingConfigRepository errorMappingConfigRepository;
  private final RedisClusterAdapter redisClusterAdapter;

  @Override
  public ErrorMappingConfig getByCode(String code) {
    return redisClusterAdapter.computeIfAbsent("pap_error_mapping:" + code, ErrorMappingConfig.class, 600,
        () -> errorMappingConfigRepository.findByCode(code).orElse(null));
  }
}
