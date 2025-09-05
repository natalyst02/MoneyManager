package vn.com.mbbank.adminportal.common.service.internal;

import vn.com.mbbank.adminportal.common.model.entity.ErrorMappingConfig;

public interface ErrorMappingConfigServiceInternal {
  ErrorMappingConfig getByCode(String code);
}
