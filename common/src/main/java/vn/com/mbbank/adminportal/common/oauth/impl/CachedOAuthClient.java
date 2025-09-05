package vn.com.mbbank.adminportal.common.oauth.impl;

import lombok.RequiredArgsConstructor;
import vn.com.mbbank.adminportal.common.oauth.AccessToken;
import vn.com.mbbank.adminportal.common.oauth.OAuthClient;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class CachedOAuthClient implements OAuthClient {
  private final OAuthClient oAuthClient;
  @SuppressWarnings("java:S3077")
  private volatile CompletableFuture<AccessToken> tokenFut;

  @Override
  public CompletableFuture<AccessToken> getAccessToken() {
    var fut = this.tokenFut;
    if (needRefreshToken(fut)) {
      fut = oAuthClient.getAccessToken();
      this.tokenFut = fut;
    }
    return fut;
  }

  public CompletableFuture<AccessToken> refreshAccessToken() {
    tokenFut = oAuthClient.getAccessToken();
    return tokenFut;
  }

  private boolean needRefreshToken(CompletableFuture<AccessToken> accessTokenFut) {
    return accessTokenFut == null || (accessTokenFut.isDone() && (accessTokenFut.isCompletedExceptionally() || accessTokenFut.join().isExpired()));
  }
}