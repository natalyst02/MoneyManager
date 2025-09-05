package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.common.mapper.BeanMapper;
import vn.com.mbbank.adminportal.core.model.entity.Role;
import vn.com.mbbank.adminportal.core.model.request.UpdateRoleRequest;

@Mapper
public interface UpdateRoleRequest2RoleMapper extends BeanMapper<UpdateRoleRequest, Role> {
  UpdateRoleRequest2RoleMapper INSTANCE = Mappers.getMapper(UpdateRoleRequest2RoleMapper.class);
}
