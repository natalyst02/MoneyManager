package vn.com.mbbank.adminportal.common.oauth;

import vn.com.mbbank.adminportal.common.oauth.impl.OAuthClientBuilder;

import java.util.concurrent.CompletableFuture;

public interface OAuthClient {
  CompletableFuture<AccessToken> getAccessToken();

  static OAuthClientBuilder builder() {
    return new OAuthClientBuilder();
  }
}