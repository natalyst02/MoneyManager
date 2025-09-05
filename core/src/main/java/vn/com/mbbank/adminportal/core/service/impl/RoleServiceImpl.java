package vn.com.mbbank.adminportal.core.service.impl;

import lombok.extern.log4j.Log4j2;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import vn.com.mbbank.adminportal.common.config.RedisClusterAdapter;
import vn.com.mbbank.adminportal.common.exception.PaymentPlatformException;
import vn.com.mbbank.adminportal.core.mapper.CreateRoleRequest2RoleMapper;
import vn.com.mbbank.adminportal.core.mapper.PermissionCategoryInfo2GetRolePermissionMapper;
import vn.com.mbbank.adminportal.core.mapper.PermissionCategoryInfo2PermissionInfoMapper;
import vn.com.mbbank.adminportal.core.mapper.Role2CreateRoleResponseMapper;
import vn.com.mbbank.adminportal.core.mapper.Role2GetRoleResponseMapper;
import vn.com.mbbank.adminportal.core.mapper.Role2RoleResponseMapper;
import vn.com.mbbank.adminportal.core.mapper.Role2UpdateRoleResponseMapper;
import vn.com.mbbank.adminportal.core.mapper.UpdateRoleRequest2RoleMapper;
import vn.com.mbbank.adminportal.core.model.PermissionAction;
import vn.com.mbbank.adminportal.core.model.PermissionCategoryInfo;
import vn.com.mbbank.adminportal.core.model.PermissionInfo;
import vn.com.mbbank.adminportal.core.model.PermissionType;
import vn.com.mbbank.adminportal.core.model.RoleType;
import vn.com.mbbank.adminportal.core.model.RolesPermissions;
import vn.com.mbbank.adminportal.core.model.entity.Role;
import vn.com.mbbank.adminportal.core.model.filter.RoleFilter;
import vn.com.mbbank.adminportal.core.model.request.CreateRoleRequest;
import vn.com.mbbank.adminportal.core.model.request.UpdateRoleRequest;
import vn.com.mbbank.adminportal.core.model.response.CreateRoleResponse;
import vn.com.mbbank.adminportal.core.model.response.GetRoleResponse;
import vn.com.mbbank.adminportal.core.model.response.RoleResponse;
import vn.com.mbbank.adminportal.core.model.response.UpdateRoleResponse;
import vn.com.mbbank.adminportal.core.repository.PermissionRepository;
import vn.com.mbbank.adminportal.core.repository.RoleRepository;
import vn.com.mbbank.adminportal.core.service.internal.PermissionServiceInternal;
import vn.com.mbbank.adminportal.core.service.internal.RoleServiceInternal;
import vn.com.mbbank.adminportal.core.service.internal.UserServiceInternal;
import vn.com.mbbank.adminportal.core.util.Authentications;
import vn.com.mbbank.adminportal.core.util.Constant;
import vn.com.mbbank.adminportal.core.util.ErrorCode;
import vn.com.mbbank.adminportal.core.util.Filters;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Log4j2
public class RoleServiceImpl implements RoleServiceInternal {
  private final RoleRepository roleRepository;
  private final PermissionRepository permissionRepository;
  private final PermissionServiceInternal permissionServiceInternal;
  private final UserServiceInternal userServiceInternal;
  private final RedisClusterAdapter redisClusterAdapter;

  public RoleServiceImpl(RoleRepository roleRepository,
                         PermissionRepository permissionRepository,
                         @Lazy PermissionServiceInternal permissionServiceInternal,
                         @Lazy UserServiceInternal userServiceInternal,
                         RedisClusterAdapter redisClusterAdapter) {
    this.roleRepository = roleRepository;
    this.permissionRepository = permissionRepository;
    this.permissionServiceInternal = permissionServiceInternal;
    this.userServiceInternal = userServiceInternal;
    this.redisClusterAdapter = redisClusterAdapter;
  }

  private static HashMap<String, Integer> evaluatePermissions(Map<String, Integer> manipulatorPermissions, List<PermissionCategoryInfo> permissions) {
    var rolePermission = new HashMap<String, Integer>();
    for (var permission : permissions) {
      boolean hasModule = manipulatorPermissions != null && manipulatorPermissions.containsKey(permission.getModule());
      boolean containPermission = hasModule && (manipulatorPermissions.get(permission.getModule()) & permission.getBitmaskValue()) == permission.getBitmaskValue();
      if (!containPermission) {
        throw new PaymentPlatformException(ErrorCode.NON_INFERIOR_ROLE, ErrorCode.NON_INFERIOR_ROLE.message());
      }
      rolePermission.merge(permission.getModule(), permission.getBitmaskValue(), Integer::sum);
    }
    return rolePermission;
  }

  @Override
  public Role getActiveRoleById(Long id) {
    return roleRepository.findByIdAndActive(id, true)
        .orElseThrow(() -> new PaymentPlatformException(ErrorCode.ROLE_NOT_FOUND, ErrorCode.ROLE_NOT_FOUND.message()));
  }

  @Override
  public Role create(Role role) {
    return roleRepository.persistAndFlush(role);
  }

  @Override
  public Role getByCode(String code) {
    return roleRepository.findByCode(code)
        .orElseThrow(() -> new PaymentPlatformException(ErrorCode.ROLE_NOT_FOUND, ErrorCode.ROLE_NOT_FOUND.message()));
  }

  @Override
  public Role updateReturning(Role role) {
    return roleRepository.updateRole(role)
        .orElseThrow(() -> new PaymentPlatformException(ErrorCode.ROLE_NOT_FOUND, ErrorCode.ROLE_NOT_FOUND.message()));
  }

  @Override
  public Role getById(Long id) {
    return roleRepository.findById(id)
        .orElseThrow(() -> new PaymentPlatformException(ErrorCode.ROLE_NOT_FOUND, ErrorCode.ROLE_NOT_FOUND.message()));
  }

  @Override
  public List<RolesPermissions> getRolesPermissionsByActiveRole(Long userId) {
    return roleRepository.getRolesPermissionsByUserId(userId, true);
  }

  @Override
  public Page<RoleResponse> getRoles(RoleFilter request, Pageable pageable) {
    var permissions = permissionServiceInternal.getAllPermission();
    return roleRepository.findAll(Filters.toSpecification(request), pageable).map(role -> {
      var resp = Role2RoleResponseMapper.INSTANCE.map(role);
      var respPermissions = new HashMap<String, List<PermissionInfo>>();
      var roleType = mapToPermissionType(role.getType());
      for (var permission : permissions) {
        if (permission.getType() != roleType) {
          continue;
        }
        var rolePermission = PermissionCategoryInfo2PermissionInfoMapper.INSTANCE.map(permission);
        var appendedPermissions = new ArrayList<PermissionInfo>();
        appendedPermissions.add(rolePermission);
        var rolePermissions = role.getPermissions();
        if (rolePermissions != null
            && rolePermissions.get(permission.getModule()) != null
            && (rolePermissions.get(permission.getModule()) & permission.getBitmaskValue()) == permission.getBitmaskValue()) {
          respPermissions.merge(permission.getModule(), appendedPermissions, this::addRolePermission);
        }
      }
      resp.setPermissions(respPermissions);
      return resp;
    });
  }

  @Override
  public CreateRoleResponse create(Authentication authentication, CreateRoleRequest request) {
    var papUser = Authentications.requirePapUser();
    if (RoleType.ADMIN.equals(request.getType())) {
      throw new PaymentPlatformException(ErrorCode.INVALID_PARAMETERS, "Invalid role type");
    }
    var permissions = permissionRepository.getPermissionsByTypeAndIdIn(mapToPermissionType(request.getType()), request.getPermissionIds());
    if (permissions.isEmpty() || permissions.size() != request.getPermissionIds().size()) {
      throw new PaymentPlatformException(ErrorCode.PERMISSION_NOT_FOUND, ErrorCode.PERMISSION_NOT_FOUND.message());
    }
    var rolePermission = evaluatePermissions(papUser.getPermissions(), permissions);
    try {
      var createdRole = create(CreateRoleRequest2RoleMapper.INSTANCE.map(request, papUser.getUsername(), rolePermission));
      return Role2CreateRoleResponseMapper.INSTANCE.map(createdRole);
    } catch (DataIntegrityViolationException e) {
      if (e.getCause() instanceof ConstraintViolationException cve && cve.getConstraintName() != null) {
        if (cve.getConstraintName().endsWith("PAP_ROLE_CODE_UINDEX")) {
          throw new PaymentPlatformException(ErrorCode.DUPLICATE_ROLE_CODE, "Role code " + request.getCode() + " already exists");
        }
      }
      throw e;
    }
  }

  @Override
  public UpdateRoleResponse update(Authentication authentication, Long id, UpdateRoleRequest request) {
    var updateRole = getById(id);
    if (RoleType.ADMIN.equals(updateRole.getType())) {
      throw new PaymentPlatformException(ErrorCode.ROLE_CANT_BE_UPDATED, ErrorCode.ROLE_CANT_BE_UPDATED.message());
    }
    var permissions = permissionRepository.getPermissionsByTypeAndIdIn(mapToPermissionType(updateRole.getType()), request.getPermissionIds());
    if (permissions.isEmpty() || permissions.size() != request.getPermissionIds().size()) {
      throw new PaymentPlatformException(ErrorCode.PERMISSION_NOT_FOUND, ErrorCode.PERMISSION_NOT_FOUND.message());
    }
    var papUser = Authentications.requirePapUser();
    var rolePermission = evaluatePermissions(papUser.getPermissions(), permissions);
    var updatedRole = updateReturning(map(request, papUser.getUsername(), rolePermission, id));
    deleteUserCacheByRoleId(id);
    return Role2UpdateRoleResponseMapper.INSTANCE.map(updatedRole);
  }

  @Override
  public GetRoleResponse get(Long id) {
    var role = getById(id);
    var rolePermissions = role.getPermissions();
    var permissions = permissionServiceInternal.getPermissionsByType(mapToPermissionType(role.getType()));
    var roleDetailPermissions = new HashMap<String, List<PermissionAction>>();
    for (var permission : permissions) {
      var roleDetailPermission = PermissionCategoryInfo2GetRolePermissionMapper.INSTANCE.map(permission);
      var appendedPermissions = new ArrayList<PermissionAction>();
      appendedPermissions.add(roleDetailPermission);
      if (rolePermissions != null && rolePermissions.get(permission.getModule()) != null && (rolePermissions.get(permission.getModule()) & permission.getBitmaskValue()) == permission.getBitmaskValue()) {
        roleDetailPermission.setSelected(true);
      }
      roleDetailPermissions.merge(permission.getModule(), appendedPermissions, this::add);
    }
    return Role2GetRoleResponseMapper.INSTANCE.map(role).setPermissions(roleDetailPermissions);
  }

  private List<PermissionAction> add(List<PermissionAction> permissions, List<PermissionAction> appendedPermissions) {
    permissions.addAll(appendedPermissions);
    return permissions;
  }

  private Role map(UpdateRoleRequest request, String username, Map<String, Integer> rolePermission, Long id) {
    return UpdateRoleRequest2RoleMapper.INSTANCE.map(request)
        .setId(id)
        .setPermissions(rolePermission)
        .setUpdatedBy(username)
        .setUpdatedAt(OffsetDateTime.now());
  }

  @Override
  public void validateRoleByIds(List<Long> roleIds) {
    var validRoleIds = roleRepository.findAllActiveRoleIdsByIds(roleIds);
    var invalidRoleIds = roleIds.stream()
        .filter(roleId -> !validRoleIds.contains(roleId))
        .toList();
    if (!invalidRoleIds.isEmpty()) {
      throw new PaymentPlatformException(ErrorCode.ROLE_ID_NOT_EXIST, "The following role IDs do not exist or have been deactivated: " + invalidRoleIds);
    }
  }

  public List<Long> findExistingRoleIds(List<Long> roleIds) {
    return roleRepository.findAllById(roleIds).stream()
        .map(Role::getId)
        .toList();
  }

  @Override
  public void deleteUserCacheByRoleId(Long roleId) {
    var users = userServiceInternal.getUsernamesByRoleId(roleId);
    if (!users.isEmpty()) {
      for (var user : users) {
        redisClusterAdapter.delete(Constant.PAP_USER_KEY + user);
      }
    }
  }

  private List<PermissionInfo> addRolePermission(List<PermissionInfo> permissions, List<PermissionInfo> appendedPermissions) {
    permissions.addAll(appendedPermissions);
    return permissions;
  }

  private PermissionType mapToPermissionType(RoleType roleType) {
    return switch (roleType) {
      case IT -> PermissionType.IT;
      case BUSINESS -> PermissionType.BUSINESS;
      case ADMIN -> PermissionType.ADMIN;
    };
  }
}
