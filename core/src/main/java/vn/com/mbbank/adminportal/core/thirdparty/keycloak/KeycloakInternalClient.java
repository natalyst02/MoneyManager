package vn.com.mbbank.adminportal.core.thirdparty.keycloak;

import vn.com.mbbank.adminportal.core.thirdparty.keycloak.model.request.AssignRoleRequest;
import vn.com.mbbank.adminportal.core.thirdparty.keycloak.model.response.GetUserInfoResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface KeycloakInternalClient {
  CompletableFuture<Boolean> assignRoleToUser(AssignRoleRequest request, String userId);

  CompletableFuture<Boolean> revokeRoleFromUser(AssignRoleRequest request, String userId);

  CompletableFuture<List<GetUserInfoResponse>> getUserInfo(String username);

  CompletableFuture<Boolean> validateToken(String token);
}
