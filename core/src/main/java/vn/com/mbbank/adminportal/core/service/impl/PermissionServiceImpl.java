package vn.com.mbbank.adminportal.core.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.com.mbbank.adminportal.common.config.RedisClusterAdapter;
import vn.com.mbbank.adminportal.core.mapper.PermissionCategoryInfo2PermissionResponseMapper;
import vn.com.mbbank.adminportal.core.model.PermissionCategoryInfo;
import vn.com.mbbank.adminportal.core.model.PermissionType;
import vn.com.mbbank.adminportal.core.model.Permissions;
import vn.com.mbbank.adminportal.core.model.response.PermissionResponse;
import vn.com.mbbank.adminportal.core.repository.PermissionRepository;
import vn.com.mbbank.adminportal.core.service.internal.PermissionServiceInternal;

import java.util.List;
import java.util.Map;

@Service
@Log4j2
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionServiceInternal {
  @Value("${redis.keys.all-permissions}")
  private String allPermissionsKey;

  private final PermissionRepository permissionRepository;
  private final RedisClusterAdapter redisClusterAdapter;

  @PostConstruct
  private void reloadPermission() {
    redisClusterAdapter.delete(allPermissionsKey);
  }

  @Override
  public boolean hasPermission(Map<String, Integer> permissions, String module, int requiredPermission) {
    var modules = permissionRepository.getAllModules();
    if (modules.isEmpty() || !modules.contains(module)) {
      return false;
    }
    if (permissions == null || permissions.get(module) == null) {
      return false;
    }
    var permission = permissions.get(module);
    return (permission & requiredPermission) != 0;
  }

  @Override
  public List<PermissionCategoryInfo> getAllPermission() {
    return redisClusterAdapter.computeIfAbsent(allPermissionsKey, Permissions.class, this::getPermissions).getPermissions();
  }

  @Override
  public List<PermissionCategoryInfo> getPermissionsByType(PermissionType type) {
    return permissionRepository.getPermissionsByType(type);
  }

  @Override
  public List<PermissionResponse> getAll() {
    return PermissionCategoryInfo2PermissionResponseMapper.INSTANCE.map(getAllPermission());
  }

  private Permissions getPermissions() {
    return new Permissions().setPermissions(permissionRepository.getAllPermission());
  }
}
