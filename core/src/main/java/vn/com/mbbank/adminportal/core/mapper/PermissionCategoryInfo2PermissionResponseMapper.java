package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.common.mapper.BeanMapper;
import vn.com.mbbank.adminportal.core.model.PermissionCategoryInfo;
import vn.com.mbbank.adminportal.core.model.response.PermissionResponse;

@Mapper
public interface PermissionCategoryInfo2PermissionResponseMapper extends BeanMapper<PermissionCategoryInfo, PermissionResponse> {
  PermissionCategoryInfo2PermissionResponseMapper INSTANCE = Mappers.getMapper(PermissionCategoryInfo2PermissionResponseMapper.class);

  PermissionResponse map(PermissionCategoryInfo source);
}
