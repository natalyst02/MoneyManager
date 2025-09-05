package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.common.mapper.BeanMapper;
import vn.com.mbbank.adminportal.core.model.PermissionAction;
import vn.com.mbbank.adminportal.core.model.PermissionCategoryInfo;

@Mapper
public interface PermissionCategoryInfo2GetRolePermissionMapper extends BeanMapper<PermissionCategoryInfo, PermissionAction> {
  PermissionCategoryInfo2GetRolePermissionMapper INSTANCE = Mappers.getMapper(PermissionCategoryInfo2GetRolePermissionMapper.class);

  @Mapping(target = "selected", ignore = true)
  PermissionAction map(PermissionCategoryInfo permission);

}
