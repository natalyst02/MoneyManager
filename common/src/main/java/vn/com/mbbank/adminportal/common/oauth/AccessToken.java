package vn.com.mbbank.adminportal.common.oauth;

import com.dslplatform.json.CompiledJson;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@CompiledJson
public class AccessToken {
  private final String token;
  private final String tokenType;
  private final long expiresAt;
  private final long expiresIn;
  private final long expiresAtMs;

  public AccessToken(String token, String tokenType, long expiresAt, long expiresIn) {
    this.token = token;
    this.tokenType = tokenType;
    this.expiresAt = expiresAt != 0 ? expiresAt : (System.currentTimeMillis() / 1000 + expiresIn);
    this.expiresIn = expiresIn;
    this.expiresAtMs = (this.expiresAt - 300) * 1000;
  }

  public boolean isExpired() {
    return System.currentTimeMillis() > expiresAtMs;
  }

  @JsonProperty("access_token")
  public String getToken() {
    return token;
  }

  @JsonProperty("token_type")
  public String getTokenType() {
    return tokenType;
  }

  @JsonProperty("expires_at")
  public long getExpiresAt() {
    return expiresAt;
  }

  @JsonProperty("expires_in")
  public long getExpiresIn() {
    return expiresIn;
  }
}