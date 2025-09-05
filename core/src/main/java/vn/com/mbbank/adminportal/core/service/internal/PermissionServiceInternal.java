package vn.com.mbbank.adminportal.core.service.internal;

import vn.com.mbbank.adminportal.core.model.PermissionCategoryInfo;
import vn.com.mbbank.adminportal.core.model.PermissionType;
import vn.com.mbbank.adminportal.core.service.PermissionService;

import java.util.List;
import java.util.Map;

public interface PermissionServiceInternal extends PermissionService {
  boolean hasPermission(Map<String, Integer> permissions, String module, int requiredPermission);

  List<PermissionCategoryInfo> getAllPermission();

  List<PermissionCategoryInfo> getPermissionsByType(PermissionType type);
}
