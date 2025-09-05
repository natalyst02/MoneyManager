package vn.com.mbbank.adminportal.core.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import vn.com.mbbank.adminportal.common.model.PapUser;
import vn.com.mbbank.adminportal.core.model.BitmaskValue;
import vn.com.mbbank.adminportal.core.service.internal.PermissionServiceInternal;

import java.io.Serializable;

@Component
@RequiredArgsConstructor
public class BitmaskPermissionEvaluator implements PermissionEvaluator {
  private final PermissionServiceInternal permissionService;

  @Override
  public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object bitmask) {
    if (authentication == null || !(authentication.getPrincipal() instanceof PapUser papUser)) {
      return false;
    }
    if (bitmask instanceof BitmaskValue bitmaskValue && targetDomainObject instanceof String module) {
      return permissionService.hasPermission(papUser.getPermissions(), module, bitmaskValue.getBitmask());
    }
    return false;
  }

  @Override
  public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
    return false;
  }
}
