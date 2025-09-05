package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.core.model.entity.Role;
import vn.com.mbbank.adminportal.core.model.response.GetRoleResponse;

@Mapper
public interface Role2GetRoleResponseMapper {
  Role2GetRoleResponseMapper INSTANCE = Mappers.getMapper(Role2GetRoleResponseMapper.class);

  @Mapping(target = "permissions", ignore = true)
  GetRoleResponse map(Role role);

}
