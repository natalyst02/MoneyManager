package vn.com.mbbank.adminportal.core.thirdparty.keycloak.impl;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.JsonWriter;
import com.dslplatform.json.runtime.Generics;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import vn.com.mbbank.adminportal.common.exception.NSTCompletionException;
import vn.com.mbbank.adminportal.common.exception.PaymentPlatformException;
import vn.com.mbbank.adminportal.common.oauth.OAuthClient;
import vn.com.mbbank.adminportal.common.util.Constant;
import vn.com.mbbank.adminportal.common.util.Json;
import vn.com.mbbank.adminportal.common.util.RestApiHelper;
import vn.com.mbbank.adminportal.common.util.RestClient;
import vn.com.mbbank.adminportal.core.thirdparty.keycloak.KeycloakInternalClient;
import vn.com.mbbank.adminportal.core.thirdparty.keycloak.model.request.AssignRoleRequest;
import vn.com.mbbank.adminportal.core.thirdparty.keycloak.model.response.AssignRoleResponse;
import vn.com.mbbank.adminportal.core.thirdparty.keycloak.model.response.GetUserInfoResponse;
import vn.com.mbbank.adminportal.core.thirdparty.keycloak.model.response.ValidateTokenResponse;
import vn.com.mbbank.adminportal.core.util.ErrorCode;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@Log4j2
public class KeycloakInternalClientImpl implements KeycloakInternalClient {
  private static final JsonWriter.WriteObject<List<AssignRoleRequest>> assignRoleReqWriter = Json.findWriter(Generics.makeParameterizedType(List.class, AssignRoleRequest.class));
  private static final JsonReader.ReadObject<AssignRoleResponse> assignRoleRespReader = Json.findReader(AssignRoleResponse.class);
  private static final JsonReader.ReadObject<List<GetUserInfoResponse>> getUserInfoRespReader = Json.findReader(Generics.makeParameterizedType(List.class, GetUserInfoResponse.class));
  private static final JsonReader.ReadObject<ValidateTokenResponse> validateTokenRespReader = Json.findReader(ValidateTokenResponse.class);
  private final RestClient restClient;
  private final OAuthClient oAuthClient;
  private final String roleMappingUrl;
  private final String userInfoUrl;
  private final String validateTokenUrl;
  private final String clientId;
  private final String clientSecret;

  public KeycloakInternalClientImpl(RestClient restClient,
                                    @Value("${keycloak.internal.url}") String url,
                                    @Value("${keycloak.internal.client-id}") String clientId,
                                    @Value("${keycloak.internal.client-secret}") String clientSecret) {
    this.restClient = restClient;
    this.oAuthClient = OAuthClient.builder()
        .setRestClient(restClient)
        .setClientId(clientId)
        .setClientSecret(clientSecret)
        .setTokenApiUrl(URI.create(url + "/realms/internal/protocol/openid-connect/token"))
        .setGrantType(Constant.OAUTH_GRANT_TYPE)
        .setUseCache(true)
        .build();
    this.roleMappingUrl = url + "/admin/realms/internal/users/";
    this.userInfoUrl = url + "/admin/realms/internal/users?briefRepresentation=true&first=0&max=50&search=";
    this.validateTokenUrl = url + "/realms/internal/protocol/openid-connect/token/introspect";
    this.clientId = clientId;
    this.clientSecret = clientSecret;
  }

  @Override
  @CircuitBreaker(name = "assignRoleToUser", fallbackMethod = "assignRoleToUserFallback")
  public CompletableFuture<Boolean> assignRoleToUser(AssignRoleRequest request, String userId) {
    return oAuthClient.getAccessToken()
        .thenCompose(accessToken -> {
          var header = Map.of(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE,
              HttpHeaders.AUTHORIZATION, "Bearer " + accessToken.getToken(),
              Constant.CLIENT_MESSAGE_ID, RestApiHelper.getOrCreateClientMessageId());
          var clientServiceId = request.getContainerId();
          return restClient.post(URI.create(roleMappingUrl + userId + "/role-mappings/clients/" + clientServiceId), header, List.of(request), assignRoleReqWriter)
              .handle((httpResponse, throwable) -> {
                if (throwable != null) {
                  RestApiHelper.handleException(throwable, ErrorCode.ASSIGN_ROLE_TIMEOUT, ErrorCode.ASSIGN_ROLE_TIMEOUT::message,
                      ErrorCode.ASSIGN_ROLE_ERROR, ErrorCode.ASSIGN_ROLE_ERROR::message);
                }
                var statusCode = httpResponse.statusCode();
                if (statusCode == 204) {
                  return true;
                }
                var response = Json.decode(httpResponse.body(), assignRoleRespReader, e ->
                    new NSTCompletionException(new PaymentPlatformException(ErrorCode.ASSIGN_ROLE_ERROR,
                        "Can not assign role to user due: " + new String(httpResponse.body(), StandardCharsets.UTF_8))));
                throw new NSTCompletionException(new PaymentPlatformException(ErrorCode.ASSIGN_ROLE_ERROR, "Can not assign role to user due: " + response.error()));
              });
        });
  }

  @Override
  @CircuitBreaker(name = "revokeRoleFromUser", fallbackMethod = "revokeRoleFromUserFallback")
  public CompletableFuture<Boolean> revokeRoleFromUser(AssignRoleRequest request, String userId) {
    return oAuthClient.getAccessToken()
        .thenCompose(accessToken -> {
          var header = Map.of(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE,
              HttpHeaders.AUTHORIZATION, "Bearer " + accessToken.getToken(),
              Constant.CLIENT_MESSAGE_ID, RestApiHelper.getOrCreateClientMessageId());
          var clientServiceId = request.getContainerId();
          return restClient.delete(URI.create(roleMappingUrl + userId + "/role-mappings/clients/" + clientServiceId), header, List.of(request), assignRoleReqWriter)
              .handle((httpResponse, throwable) -> {
                if (throwable != null) {
                  RestApiHelper.handleException(throwable, ErrorCode.REVOKE_ROLE_TIMEOUT, ErrorCode.REVOKE_ROLE_TIMEOUT::message,
                      ErrorCode.REVOKE_ROLE_ERROR, ErrorCode.REVOKE_ROLE_ERROR::message);
                }
                var statusCode = httpResponse.statusCode();
                if (statusCode == 204) {
                  return true;
                }
                var response = Json.decode(httpResponse.body(), assignRoleRespReader, e ->
                    new NSTCompletionException(new PaymentPlatformException(ErrorCode.REVOKE_ROLE_ERROR,
                        "Can not revoke role from user due: " + new String(httpResponse.body(), StandardCharsets.UTF_8))));
                throw new NSTCompletionException(new PaymentPlatformException(ErrorCode.REVOKE_ROLE_ERROR, "Can not revoke role from user due: " + response.error()));
              });
        });
  }

  @Override
  @CircuitBreaker(name = "getKeycloakUserInfo", fallbackMethod = "getKeycloakUserInfoFallback")
  public CompletableFuture<List<GetUserInfoResponse>> getUserInfo(String username) {
     return oAuthClient.getAccessToken()
        .thenCompose(accessToken -> {
          var header = Map.of(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE,
              HttpHeaders.AUTHORIZATION, "Bearer " + accessToken.getToken(),
              Constant.CLIENT_MESSAGE_ID, RestApiHelper.getOrCreateClientMessageId());
          return restClient.get(URI.create(userInfoUrl + username), header)
              .handle((httpResponse, throwable) -> {
                if (throwable != null) {
                  RestApiHelper.handleException(throwable, ErrorCode.GET_KEYCLOAK_USER_INFO_TIMEOUT, ErrorCode.GET_KEYCLOAK_USER_INFO_TIMEOUT::message,
                      ErrorCode.GET_KEYCLOAK_USER_INFO_ERROR, ErrorCode.GET_KEYCLOAK_USER_INFO_ERROR::message);
                }
                var status = httpResponse.statusCode();
                if (status == 200) {
                  var response = Json.decode(httpResponse.body(), getUserInfoRespReader, e ->
                      new NSTCompletionException(new PaymentPlatformException(ErrorCode.GET_KEYCLOAK_USER_INFO_ERROR,
                          "Can not get user info due: " + new String(httpResponse.body(), StandardCharsets.UTF_8))));
                  if (!response.isEmpty()) {
                    return response;
                  }
                  throw new NSTCompletionException(new PaymentPlatformException(ErrorCode.GET_KEYCLOAK_USER_NOT_FOUND, "Keycloak user " + username + " not found"));
                }
                throw new NSTCompletionException(new PaymentPlatformException(ErrorCode.GET_KEYCLOAK_USER_INFO_ERROR, "Can not get user info due: " + new String(httpResponse.body(), StandardCharsets.UTF_8)));
              });
        });
  }

  @Override
  @CircuitBreaker(name = "validateToken", fallbackMethod = "validateTokenFallback")
  public CompletableFuture<Boolean> validateToken(String token) {
    var requestBody = generateRequestBody(clientId, clientSecret, token);
    var request = HttpRequest.newBuilder(URI.create(validateTokenUrl))
        .header("content-type", "application/x-www-form-urlencoded")
        .POST(HttpRequest.BodyPublishers.ofByteArray(requestBody.getBytes(StandardCharsets.UTF_8)))
        .build();
    return restClient.sendAsync(request, true)
        .handle((httpResponse, throwable) -> {
          if (throwable != null) {
            RestApiHelper.handleException(throwable, ErrorCode.VALIDATE_TOKEN_TIMEOUT, ErrorCode.VALIDATE_TOKEN_TIMEOUT::message,
                ErrorCode.VALIDATE_TOKEN_ERROR, ErrorCode.VALIDATE_TOKEN_ERROR::message);
          }
          var status = httpResponse.statusCode();
          if (status == 200) {
            var response = Json.decode(httpResponse.body(), validateTokenRespReader, e ->
                new NSTCompletionException(new PaymentPlatformException(ErrorCode.VALIDATE_TOKEN_ERROR,
                    "Can not validate token due: " + new String(httpResponse.body(), StandardCharsets.UTF_8))));
            return response.isActive();
          }
          throw new NSTCompletionException(new PaymentPlatformException(ErrorCode.VALIDATE_TOKEN_ERROR, "Can not validate token due: " + new String(httpResponse.body(), StandardCharsets.UTF_8)));
        });
  }

  private String generateRequestBody(String clientId, String clientSecret, String token) {
    var formData = new StringBuilder();
    buildFormData(formData, "client_id", clientId);
    buildFormData(formData, "client_secret", clientSecret);
    buildFormData(formData, "token", token);
    if (!formData.isEmpty()) {
      formData.deleteCharAt(formData.length() - 1);
    }
    return formData.toString();
  }

  private void buildFormData(StringBuilder builder, String fieldName, String value) {
    if (value != null) {
      builder.append(fieldName).append('=').append(URLEncoder.encode(value, StandardCharsets.UTF_8)).append('&');
    }
  }

  public CompletableFuture<Boolean> assignRoleToUserFallback(AssignRoleRequest request, String userId, CallNotPermittedException e) {
    log.error("Keycloak AssignRoleToUserFallback: Assign role {} to user {} error", request.getId(), userId, e);
    return CompletableFuture.failedFuture(new PaymentPlatformException(ErrorCode.ASSIGN_ROLE_ERROR, ErrorCode.ASSIGN_ROLE_ERROR.message(), e));
  }

  public CompletableFuture<Boolean> revokeRoleFromUserFallback(AssignRoleRequest request, String userId, CallNotPermittedException e) {
    log.error("Keycloak RevokeRoleFromUserFallback: Revoke role {} from user {} error", request.getId(), userId, e);
    return CompletableFuture.failedFuture(new PaymentPlatformException(ErrorCode.REVOKE_ROLE_ERROR, ErrorCode.REVOKE_ROLE_ERROR.message(), e));
  }

  public CompletableFuture<List<GetUserInfoResponse>> getKeycloakUserInfoFallback(String username, CallNotPermittedException e) {
    log.error("Keycloak GetKeycloakUserInfoFallback: Get keycloak user info with user {} error", username, e);
    return CompletableFuture.failedFuture(new PaymentPlatformException(ErrorCode.GET_KEYCLOAK_USER_INFO_ERROR, ErrorCode.GET_KEYCLOAK_USER_INFO_ERROR.message(), e));
  }

  public CompletableFuture<Boolean> validateTokenFallback(String token, CallNotPermittedException e) {
    log.error("Keycloak ValidateTokenFallback: Validate token error", e);
    return CompletableFuture.failedFuture(new PaymentPlatformException(ErrorCode.VALIDATE_TOKEN_ERROR, ErrorCode.VALIDATE_TOKEN_ERROR.message(), e));
  }
}
