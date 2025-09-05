package vn.com.mbbank.adminportal.core.service.impl;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;
import vn.com.mbbank.adminportal.common.config.RedisClusterAdapter;
import vn.com.mbbank.adminportal.common.exception.NSTCompletionException;
import vn.com.mbbank.adminportal.common.exception.PaymentPlatformException;
import vn.com.mbbank.adminportal.common.util.CommonErrorCode;
import vn.com.mbbank.adminportal.common.util.CompletableFutures;
import vn.com.mbbank.adminportal.core.mapper.CreateUserRequest2UserMapper;
import vn.com.mbbank.adminportal.core.mapper.UpdateUserRequest2UserMapper;
import vn.com.mbbank.adminportal.core.mapper.User2UserResponseMapper;
import vn.com.mbbank.adminportal.core.model.BitmaskValue;
import vn.com.mbbank.adminportal.core.model.entity.User;
import vn.com.mbbank.adminportal.core.model.filter.UserFilter;
import vn.com.mbbank.adminportal.core.model.request.CreateUserRequest;
import vn.com.mbbank.adminportal.core.model.request.LoginRequest;
import vn.com.mbbank.adminportal.core.model.request.UpdateUserRequest;
import vn.com.mbbank.adminportal.core.model.response.AuthResponse;
import vn.com.mbbank.adminportal.core.model.response.UserPermissionsResp;
import vn.com.mbbank.adminportal.core.model.response.UserResponse;
import vn.com.mbbank.adminportal.core.repository.UserRepository;
import vn.com.mbbank.adminportal.core.service.internal.PermissionServiceInternal;
import vn.com.mbbank.adminportal.core.service.internal.RoleServiceInternal;
import vn.com.mbbank.adminportal.core.service.internal.UserRoleServiceInternal;
import vn.com.mbbank.adminportal.core.service.internal.UserServiceInternal;
import vn.com.mbbank.adminportal.core.thirdparty.hcm.HcmClient;
import vn.com.mbbank.adminportal.core.thirdparty.hcm.model.GetHcmUserInfoResponse;
import vn.com.mbbank.adminportal.core.thirdparty.keycloak.KeycloakInternalClient;
import vn.com.mbbank.adminportal.core.thirdparty.keycloak.model.request.AssignRoleRequest;
import vn.com.mbbank.adminportal.core.thirdparty.keycloak.model.response.GetUserInfoResponse;
import vn.com.mbbank.adminportal.core.util.Authentications;
import vn.com.mbbank.adminportal.core.util.Constant;
import vn.com.mbbank.adminportal.core.util.ErrorCode;
import vn.com.mbbank.adminportal.core.util.JwtUtil;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@Log4j2
public class UserServiceImpl implements UserServiceInternal {
  private final UserRepository userRepository;
  private final TransactionTemplate transactionTemplate;
  private final HcmClient hcmClient;
  private final KeycloakInternalClient keycloakInternalClient;
  private final RoleServiceInternal roleService;
  private final UserRoleServiceInternal userRoleService;
  private final PermissionEvaluator permissionEvaluator;
  private final AssignRoleRequest assignRoleRequest;
  private final PermissionServiceInternal permissionService;
  private final RedisClusterAdapter redisClusterAdapter;

  private final JwtUtil jwtUtil;

  public UserServiceImpl(UserRepository userRepository,
                         @Value("${keycloak.internal.client-service-id}") String clientServiceId,
                         @Value("${keycloak.internal.default-role-id}") String defaultRoleId,
                         TransactionTemplate transactionTemplate, HcmClient hcmClient,
                         KeycloakInternalClient keycloakInternalClient,
                         RoleServiceInternal roleService, UserRoleServiceInternal userRoleService,
                         PermissionEvaluator permissionEvaluator,
                         PermissionServiceInternal permissionService,
                         RedisClusterAdapter redisClusterAdapter, JwtUtil jwtUtil) {
    this.userRepository = userRepository;
    this.transactionTemplate = transactionTemplate;
    this.hcmClient = hcmClient;
    this.keycloakInternalClient = keycloakInternalClient;
    this.roleService = roleService;
    this.userRoleService = userRoleService;
    this.permissionEvaluator = permissionEvaluator;
    this.permissionService = permissionService;
    this.redisClusterAdapter = redisClusterAdapter;
      this.jwtUtil = jwtUtil;
      this.assignRoleRequest = new AssignRoleRequest()
        .setId(defaultRoleId)
        .setClientRole(true)
        .setComposite(false)
        .setName(Constant.DEFAULT_ROLE)
        .setContainerId(clientServiceId);
  }

  @Override
  public User getActiveUserByUsername(String username) {
    return userRepository.findByUsernameAndActive(username, true)
        .orElseThrow(() -> new PaymentPlatformException(ErrorCode.USER_NOT_FOUND, ErrorCode.USER_NOT_FOUND.message()));
  }

  @Override
  public List<String> getUsernamesByRoleId(Long roleID) {
    return userRepository.getUsernamesByRoleId(roleID);
  }

  @Override
  @Transactional
  public UserResponse update(Authentication authentication, Long id, UpdateUserRequest request) {
    var papUser = Authentications.requirePapUser();
    if (request.getActive() != null && papUser.getUserId().equals(id) && !request.getActive()) {
      throw new PaymentPlatformException(ErrorCode.SELF_DEACTIVATE_ERROR, "user with id " + id + "can not deactivate itself");
    }

    if (!CollectionUtils.isEmpty(request.getRoleIds())) {
      if (!permissionEvaluator.hasPermission(authentication, "role", BitmaskValue.ASSIGN)) {
        throw new PaymentPlatformException(CommonErrorCode.FORBIDDEN, "You don't have permission to assign role");
      }
    }

    var username = papUser.getUsername();
    if (request.getRoleIds() != null) {
      userRoleService.updateUserRoles(username, id, request.getRoleIds());
    }
    var isActive = request.getActive();
    var preUpdatedUser = userRepository.getLockedUserById(id)
        .orElseThrow(() -> new PaymentPlatformException(ErrorCode.USER_NOT_FOUND, "Can not find user with userId: " + id));
    var user = UpdateUserRequest2UserMapper.INSTANCE.map(request)
        .setId(id)
        .setUpdatedBy(username)
        .setUpdatedAt(OffsetDateTime.now());
    if (isActive == null){
      user.setActive(preUpdatedUser.isActive());
    }
    var updatedUser = userRepository.updateUser(user)
        .orElseThrow(() -> new PaymentPlatformException(ErrorCode.USER_NOT_FOUND, "Can not find user with userId: " + id));

    if (isActive != null && !isActive.equals(preUpdatedUser.isActive())) {
      if (!isActive) {
        keycloakInternalClient.revokeRoleFromUser(assignRoleRequest, updatedUser.getKeycloakId())
            .exceptionally(t -> {
              throw new NSTCompletionException(new PaymentPlatformException(ErrorCode.REVOKE_ROLE_ERROR, "Can not revoke role from user id: " + updatedUser.getId()));
            }).join();
      } else {
        keycloakInternalClient.assignRoleToUser(assignRoleRequest, updatedUser.getKeycloakId())
            .exceptionally(t -> {
              throw new NSTCompletionException(new PaymentPlatformException(ErrorCode.ASSIGN_ROLE_ERROR, "Can not assign role from user id: " + updatedUser.getId()));
            }).join();
      }
    }
    redisClusterAdapter.delete(Constant.PAP_USER_KEY + updatedUser.getUsername());
    return userRepository.findUserById(id).get();
  }

  public Page<UserResponse> getUsers(Authentication authentication, UserFilter filter, Pageable pageable) {
    Authentications.requirePapUser();
    return userRepository.findUsers(
        filter.getUsername(),
        filter.getFullName(),
        filter.getPhoneNumber(),
        filter.getActive(),
        filter.getRoleCode(),
        filter.getRoleName(),
        pageable
    );
  }

  public CompletableFuture<GetHcmUserInfoResponse> getHcmUser(Authentication authentication, String userName) {
    Authentications.requirePapUser(authentication);
    return hcmClient.getUser(userName);
  }

  @Transactional
  public CompletableFuture<UserResponse> create(Authentication authentication, CreateUserRequest request) {
    if (!CollectionUtils.isEmpty(request.getRoleIds())) {
      if (!permissionEvaluator.hasPermission(authentication, "role", BitmaskValue.ASSIGN)) {
        throw new PaymentPlatformException(CommonErrorCode.FORBIDDEN, "You don't have permission to assign role");
      }
      roleService.validateRoleByIds(request.getRoleIds());
    }
    var createdBy = Authentications.requireUsername(authentication);
    var createdAt = OffsetDateTime.now();
    var user = CreateUserRequest2UserMapper.INSTANCE.map(request)
        .setCreatedBy(createdBy)
        .setCreatedAt(createdAt)
        .setUpdatedBy(createdBy)
        .setUpdatedAt(createdAt);

    CompletableFuture<GetHcmUserInfoResponse> hcmUserFuture = hcmClient.getUser(user.getUsername());
    CompletableFuture<List<GetUserInfoResponse>> keycloakUserFuture = keycloakInternalClient.getUserInfo(user.getUsername());

    return hcmUserFuture.thenCombine(keycloakUserFuture, (getUserInfoHcmResponse, userInfoResponseList) -> {
      GetUserInfoResponse keycloakUser = userInfoResponseList.stream()
          .filter(userInfo -> userInfo.isEnabled() && userInfo.getUsername().equals(user.getUsername()))
          .findFirst()
          .orElseThrow(() -> new NSTCompletionException(new PaymentPlatformException(
              ErrorCode.GET_KEYCLOAK_USER_BUT_NOT_FOUND_ACTIVE,
              "Keycloak user " + user.getUsername() + " not found"
          )));
      user.setKeycloakId(keycloakUser.getId())
          .setEmail(getUserInfoHcmResponse.getEmail())
          .setFullName(getUserInfoHcmResponse.getFullName())
          .setEmployeeCode(getUserInfoHcmResponse.getEmployeeCode())
          .setOrgName(getOrgName(getUserInfoHcmResponse))
          .setJobName(getUserInfoHcmResponse.getJobName())
          .setPhoneNumber(getUserInfoHcmResponse.getMobileNumber());
      return createUserAndUserRole(createdBy, user, request);
    }).thenCompose(persistedUser -> keycloakInternalClient.assignRoleToUser(assignRoleRequest, persistedUser.getKeycloakId())
        .thenApply(v -> {
          var roles = userRoleService.findRolesByUserId(persistedUser.getId());
          return User2UserResponseMapper.INSTANCE.map(persistedUser, roles);
        })
        .exceptionally(t -> {
          rollbackCreateUserAndUserRole(persistedUser.getId());
          throw CompletableFutures.toCompletionException(t);
        })).exceptionally(e -> {
      throw CompletableFutures.toCompletionException(e);
    });
  }

  private User createUserAndUserRole(String createdBy, User user, CreateUserRequest request) {
    return transactionTemplate.execute(status -> {
      try {
        var persistedUser = userRepository.persistAndFlush(user);
        userRoleService.createUserRoles(createdBy, persistedUser.getId(), request.getRoleIds());
        return persistedUser;
      } catch (Throwable t) {
        if (t instanceof DataIntegrityViolationException && t.getCause() instanceof ConstraintViolationException cve
            && cve.getConstraintName() != null
            && cve.getConstraintName().endsWith("PAP_USER_USERNAME_UINDEX")) {
          throw new PaymentPlatformException(ErrorCode.DUPLICATE_USER,
              "User with username: " + request.getUsername() + " already exists", t);
        }
        throw t;
      }
    });
  }

  private void rollbackCreateUserAndUserRole(Long userId) {
    transactionTemplate.executeWithoutResult(status -> {
      userRepository.deleteById(userId);
      userRoleService.removeUserRoles(userId);
    });
  }

  @Override
  public UserResponse getUser(Long id) {
    return userRepository.findUserById(id)
        .orElseThrow(() -> new PaymentPlatformException(ErrorCode.USER_NOT_FOUND, ErrorCode.USER_NOT_FOUND.message()));
  }

  @Override
  public List<UserPermissionsResp> getPermissions() {
    var user = Authentications.requirePapUser();
    var permissions = permissionService.getAllPermission();
    var modulesPermissions = new HashMap<String, Set<String>>();
    permissions.forEach(p -> {
      var actions = new HashSet<String>();
      actions.add(p.getAction());
      if (user.getPermissions().containsKey(p.getModule()) && ((user.getPermissions().get(p.getModule()) & p.getBitmaskValue()) != 0)) {
        modulesPermissions.merge(p.getModule(), actions, this::mergeByModule);
      }
    });
    var resp = new ArrayList<UserPermissionsResp>();
    for(var key : modulesPermissions.keySet()) {
      resp.add(new UserPermissionsResp().setModule(key).setActions(modulesPermissions.get(key)));
    }
    return resp;
  }

  @Override
  public UserResponse getPersonalInfo(Authentication authentication) {
    var username = Authentications.requireUsername(authentication);
    var user = userRepository.findByUsernameAndActive(username, true)
        .orElseThrow(() -> new PaymentPlatformException(ErrorCode.USER_NOT_FOUND, ErrorCode.USER_NOT_FOUND.message()));
    var roles = userRoleService.findRolesByUserId(user.getId());
    return User2UserResponseMapper.INSTANCE.map(user, roles);
  }

  private Set<String> mergeByModule(Set<String> actions, Set<String> appendAction) {
    actions.addAll(appendAction);
    return actions;
  }

  private String getOrgName(GetHcmUserInfoResponse getUserInfoHcmResponse) {
    String[] orgNames = {
        getUserInfoHcmResponse.getOrgNameManage(),
        getUserInfoHcmResponse.getOrgNameLevel1(),
        getUserInfoHcmResponse.getOrgNameLevel2(),
        getUserInfoHcmResponse.getOrgNameLevel3()
    };
    var orgNameBuilder = new StringBuilder();
    for (var orgName : orgNames) {
      if (orgName != null) {
        if (!orgNameBuilder.isEmpty()) orgNameBuilder.append(" - ");
        orgNameBuilder.append(orgName);
      }
    }
    return orgNameBuilder.toString();
  }

  public AuthResponse login(LoginRequest loginRequest) {
    User user =  userRepository.findByUsernameAndActive(loginRequest.getUsername(), true)
            .orElseThrow(() -> new PaymentPlatformException(ErrorCode.USERNAME_PASSWORD_INVALID, ErrorCode.USERNAME_PASSWORD_INVALID.message()));
    if (!StringUtils.equals(user.getPassword(), loginRequest.getPassword())) {
      throw new PaymentPlatformException(ErrorCode.USERNAME_PASSWORD_INVALID, ErrorCode.USERNAME_PASSWORD_INVALID.message());
    }
    String token = jwtUtil.generateToken(loginRequest.getUsername());
    AuthResponse response = new AuthResponse(
            token,
            loginRequest.getUsername(),
            jwtUtil.getExpirationTime(token)
    );
//      redisClusterAdapter.set("4324", 300, true);
    return response;

  }
}
