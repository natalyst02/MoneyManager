package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.common.mapper.BeanMapper;
import vn.com.mbbank.adminportal.core.model.entity.Role;
import vn.com.mbbank.adminportal.core.model.request.CreateRoleRequest;

import java.time.OffsetDateTime;
import java.util.Map;

@Mapper
public interface CreateRoleRequest2RoleMapper extends BeanMapper<CreateRoleRequest, Role> {
  CreateRoleRequest2RoleMapper INSTANCE = Mappers.getMapper(CreateRoleRequest2RoleMapper.class);

  @Mapping(target = "permissions", ignore = true)
  Role map(CreateRoleRequest createRoleRequest, String username, Map<String, Integer> rolePermission);

  @AfterMapping
  default void afterMapping(String username, Map<String, Integer> rolePermission, @MappingTarget Role target) {
    target.setCreatedBy(username);
    target.setCreatedAt(OffsetDateTime.now());
    target.setUpdatedBy(username);
    target.setUpdatedAt(OffsetDateTime.now());
    target.setPermissions(rolePermission);
  }
}
