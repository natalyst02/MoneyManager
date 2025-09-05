package vn.com.mbbank.adminportal.core.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import vn.com.mbbank.adminportal.common.exception.PaymentPlatformException;
import vn.com.mbbank.adminportal.core.mapper.UserRole2UserRoleHistory;
import vn.com.mbbank.adminportal.core.model.Action;
import vn.com.mbbank.adminportal.core.model.RoleInfo;
import vn.com.mbbank.adminportal.core.model.entity.UserRole;
import vn.com.mbbank.adminportal.core.model.entity.UserRoleHistory;
import vn.com.mbbank.adminportal.core.repository.RoleRepository;
import vn.com.mbbank.adminportal.core.repository.UserRepository;
import vn.com.mbbank.adminportal.core.repository.UserRoleHistoryRepository;
import vn.com.mbbank.adminportal.core.repository.UserRoleRepository;
import vn.com.mbbank.adminportal.core.service.internal.UserRoleServiceInternal;
import vn.com.mbbank.adminportal.core.util.ErrorCode;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class UserRoleServiceImpl implements UserRoleServiceInternal {
  private final UserRoleRepository userRoleRepository;
  private final RoleRepository roleRepository;
  private final UserRoleHistoryRepository userRoleHistoryRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public void updateUserRoles(String updatedBy, Long userId, List<Long> roleIds) {
    if (roleIds == null) {
      return;
    }
    var validRoleIds = roleRepository.findAllActiveRoleIdsByIds(roleIds);
    var invalidRoleIds = roleIds.stream()
        .filter(roleId -> !validRoleIds.contains(roleId))
        .toList();
    if (!invalidRoleIds.isEmpty()) {
      throw new PaymentPlatformException(ErrorCode.ROLE_ID_NOT_EXIST, "The following role IDs do not exist or have been deactivated: " + invalidRoleIds);
    }
    userRepository.getLockedUserById(userId)
        .orElseThrow(() -> new PaymentPlatformException(ErrorCode.USER_NOT_FOUND, "User with userId:  " + userId + " not found"));
    var existedUserRoles = userRoleRepository.getUserRoleByUserId(userId);
    var existingRoleIds = existedUserRoles.stream()
        .map(UserRole::getRoleId)
        .toList();
    var removedUserRoleIds = existedUserRoles.stream()
        .filter(userRole -> !roleIds.contains(userRole.getRoleId()))
        .map(UserRole::getId)
        .toList();
    var updatedAt = OffsetDateTime.now();
    if (roleIds.isEmpty()) {
      removeRolesAndSaveHistory(removedUserRoleIds, updatedBy, updatedAt);
      return;
    }
    if (!removedUserRoleIds.isEmpty()) {
      removeRolesAndSaveHistory(removedUserRoleIds, updatedBy, updatedAt);
    }
    var rolesToAdd = roleIds.stream()
        .filter(roleId -> !existingRoleIds.contains(roleId))
        .map(roleId -> new UserRole()
            .setUserId(userId)
            .setRoleId(roleId)
            .setCreatedAt(updatedAt)
            .setCreatedBy(updatedBy)
            .setUpdatedAt(updatedAt)
            .setUpdatedBy(updatedBy))
        .toList();
    if (!rolesToAdd.isEmpty()) {
      userRoleRepository.persistAllAndFlush(rolesToAdd);
    }
  }

  @Override
  public void createUserRoles(String createdBy, Long userId, List<Long> roleIds) {
    if (CollectionUtils.isEmpty(roleIds)) {
      return;
    }

    var createdAt = OffsetDateTime.now();

    var userRoles = roleIds.stream()
        .map(roleId -> new UserRole()
            .setUserId(userId)
            .setRoleId(roleId)
            .setCreatedAt(createdAt)
            .setCreatedBy(createdBy)
            .setUpdatedAt(createdAt)
            .setUpdatedBy(createdBy)
        ).toList();

    userRoleRepository.persistAllAndFlush(userRoles);
  }

  @Override
  public void removeUserRoles(Long userId) {
    userRoleRepository.deleteByUserId(userId);
  }

  public List<RoleInfo> findRolesByUserId(Long userId) {
    return userRoleRepository.findRolesByUserId(userId);
  }

  @Transactional
  public void removeRolesAndSaveHistory(List<Long> removedUserRoleIds, String updatedBy, OffsetDateTime updatedAt) {
    var deletedUserRole = delete(removedUserRoleIds);
    List<UserRoleHistory> userRoleHistories = deletedUserRole.stream()
        .map(userRole -> UserRole2UserRoleHistory.INSTANCE.map(Action.DELETE, updatedBy, userRole, updatedAt))
        .collect(Collectors.toList());
    userRoleHistoryRepository.persistAllAndFlush(userRoleHistories);
  }

  @Override
  @Transactional
  public List<UserRole> delete(List<Long> userRoleIds) {
    var userRoles = userRoleRepository.deleteUserRole(userRoleIds)
        .orElseThrow(() -> new PaymentPlatformException(ErrorCode.USER_ROLE_NOT_FOUND, "User role with id " + userRoleIds + " not found"));
    if (userRoles.size() != userRoleIds.size()) {
      throw new PaymentPlatformException(ErrorCode.USER_ROLE_NOT_FOUND, "wrong userRoleId");
    }
    return userRoles;
  }
}
