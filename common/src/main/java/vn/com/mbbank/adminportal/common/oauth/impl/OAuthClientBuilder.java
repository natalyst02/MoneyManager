package vn.com.mbbank.adminportal.common.oauth.impl;

import lombok.Setter;
import lombok.experimental.Accessors;
import vn.com.mbbank.adminportal.common.oauth.OAuthClient;
import vn.com.mbbank.adminportal.common.util.RestClient;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Setter
@Accessors(chain = true)
public class OAuthClientBuilder {
  RestClient restClient;
  URI tokenApiUrl;
  String grantType;
  String clientId;
  String clientSecret;
  String scope;
  String username;
  String password;
  private boolean useCache;

  public OAuthClient build() {
    var client = new OAuthClientImpl(this);
    return useCache ? new CachedOAuthClient(client) : client;
  }
}