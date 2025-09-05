package vn.com.mbbank.adminportal.common.thirdparty.keycloak;

import vn.com.mbbank.adminportal.common.jws.JWKSet;

import java.util.concurrent.CompletableFuture;

public interface KeycloakClient {
  CompletableFuture<JWKSet> getJWKSet();
}
