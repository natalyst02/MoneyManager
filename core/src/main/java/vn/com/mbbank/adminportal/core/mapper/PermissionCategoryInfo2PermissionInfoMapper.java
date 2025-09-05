package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.core.model.PermissionCategoryInfo;
import vn.com.mbbank.adminportal.core.model.PermissionInfo;

@Mapper
public interface PermissionCategoryInfo2PermissionInfoMapper {
  PermissionCategoryInfo2PermissionInfoMapper INSTANCE = Mappers.getMapper(PermissionCategoryInfo2PermissionInfoMapper.class);

  PermissionInfo map(PermissionCategoryInfo source);
}
