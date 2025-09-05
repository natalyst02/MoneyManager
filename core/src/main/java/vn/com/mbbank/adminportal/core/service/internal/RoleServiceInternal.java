package vn.com.mbbank.adminportal.core.service.internal;

import vn.com.mbbank.adminportal.core.model.RolesPermissions;
import vn.com.mbbank.adminportal.core.model.entity.Role;
import vn.com.mbbank.adminportal.core.service.RoleService;

import java.util.List;

public interface RoleServiceInternal extends RoleService {
  Role getActiveRoleById(Long id);

  Role create(Role role);

  Role getByCode(String code);

  Role updateReturning(Role role);

  List<RolesPermissions> getRolesPermissionsByActiveRole(Long userId);

  Role getById(Long id);

  void validateRoleByIds(List<Long> roleIds);

  List<Long> findExistingRoleIds(List<Long> roleIds);

  void deleteUserCacheByRoleId(Long roleId);
}