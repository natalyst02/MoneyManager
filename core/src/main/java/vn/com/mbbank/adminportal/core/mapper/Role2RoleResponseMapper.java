package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.core.model.response.RoleResponse;
import vn.com.mbbank.adminportal.core.model.entity.Role;

@Mapper
public interface Role2RoleResponseMapper {
  Role2RoleResponseMapper INSTANCE = Mappers.getMapper(Role2RoleResponseMapper.class);

  @Mapping(target = "permissions", ignore = true)
  RoleResponse map(Role role);
}
