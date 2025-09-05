package vn.com.mbbank.adminportal.core.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.RequestBody;
import vn.com.mbbank.adminportal.core.model.request.CreateUserRequest;
import vn.com.mbbank.adminportal.core.model.request.LoginRequest;
import vn.com.mbbank.adminportal.core.model.request.UpdateUserRequest;
import vn.com.mbbank.adminportal.core.model.filter.UserFilter;
import vn.com.mbbank.adminportal.core.model.response.AuthResponse;
import vn.com.mbbank.adminportal.core.model.response.UserPermissionsResp;
import vn.com.mbbank.adminportal.core.model.response.UserResponse;
import vn.com.mbbank.adminportal.core.thirdparty.hcm.model.GetHcmUserInfoResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;


public interface UserService {
  UserResponse update(Authentication authentication, Long id, UpdateUserRequest request);

  Page<UserResponse> getUsers(Authentication authentication, UserFilter request, Pageable pageable);

  CompletableFuture<GetHcmUserInfoResponse> getHcmUser(Authentication authentication, String userName);

  CompletableFuture<UserResponse> create(CreateUserRequest request);

  UserResponse getUser(Long id);

  List<UserPermissionsResp> getPermissions();

  UserResponse getPersonalInfo(Authentication authentication);

  AuthResponse login(LoginRequest loginRequest);
}