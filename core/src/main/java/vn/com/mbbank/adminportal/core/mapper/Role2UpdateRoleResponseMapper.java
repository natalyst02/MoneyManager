package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.common.mapper.BeanMapper;
import vn.com.mbbank.adminportal.core.model.entity.Role;
import vn.com.mbbank.adminportal.core.model.response.UpdateRoleResponse;

@Mapper
public interface Role2UpdateRoleResponseMapper extends BeanMapper<Role, UpdateRoleResponse> {
  Role2UpdateRoleResponseMapper INSTANCE = Mappers.getMapper(Role2UpdateRoleResponseMapper.class);
}
