package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.common.mapper.BeanMapper;
import vn.com.mbbank.adminportal.core.model.entity.Role;
import vn.com.mbbank.adminportal.core.model.response.CreateRoleResponse;

@Mapper
public interface Role2CreateRoleResponseMapper extends BeanMapper<Role, CreateRoleResponse> {
  Role2CreateRoleResponseMapper INSTANCE = Mappers.getMapper(Role2CreateRoleResponseMapper.class);
}
