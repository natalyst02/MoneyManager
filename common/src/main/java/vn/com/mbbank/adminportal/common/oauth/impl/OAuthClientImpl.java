package vn.com.mbbank.adminportal.common.oauth.impl;

import com.dslplatform.json.JsonReader;
import lombok.extern.log4j.Log4j2;
import vn.com.mbbank.adminportal.common.exception.NSTCompletionException;
import vn.com.mbbank.adminportal.common.exception.OAuthException;
import vn.com.mbbank.adminportal.common.oauth.AccessToken;
import vn.com.mbbank.adminportal.common.oauth.OAuthClient;
import vn.com.mbbank.adminportal.common.util.CompletableFutures;
import vn.com.mbbank.adminportal.common.util.Json;
import vn.com.mbbank.adminportal.common.util.RestClient;

import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

@Log4j2
public class OAuthClientImpl implements OAuthClient {
  private static final JsonReader.ReadObject<AccessToken> objectReader = Json.findReader(AccessToken.class);
  private final RestClient restClient;
  private final HttpRequest request;
  private final String clientId;

  OAuthClientImpl(OAuthClientBuilder builder) {
    this.restClient = builder.restClient;
    this.clientId = builder.clientId;
    var requestBody = generateRequestBody(builder);
    request = HttpRequest.newBuilder(builder.tokenApiUrl)
        .header("content-type", "application/x-www-form-urlencoded")
        .POST(HttpRequest.BodyPublishers.ofByteArray(requestBody.getBytes(StandardCharsets.UTF_8)))
        .build();
  }

  String generateRequestBody(OAuthClientBuilder builder) {
    var formData = new StringBuilder();
    buildFormData(formData, "grant_type", builder.grantType);
    buildFormData(formData, "client_id", builder.clientId);
    buildFormData(formData, "client_secret", builder.clientSecret);
    buildFormData(formData, "scope", builder.scope);
    buildFormData(formData, "username", builder.username);
    buildFormData(formData, "password", builder.password);
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

  @Override
  public CompletableFuture<AccessToken> getAccessToken() {
    log.info("POST {}, clientId: {}", request.uri(), clientId);
    return restClient.sendAsync(request, false)
        .handle((response, t) -> {
          if (t != null) {
            var cause = CompletableFutures.unwrapException(t);
            throw new NSTCompletionException(new OAuthException("Can't get access token from uri: " + request.uri(), cause));
          }
          if (response.statusCode() == 200) {
            log.info("POST {}, clientId: {}, response code = 200", request.uri(), clientId);
            return Json.decode(response.body(), objectReader, e -> new NSTCompletionException(
                new OAuthException("Can't get access token from uri: " + request.uri() + " due: " + new String(response.body(), StandardCharsets.UTF_8))));
          }
          throw new NSTCompletionException(new OAuthException("Can't get access token from uri: " + request.uri() + " due: " + new String(response.body(), StandardCharsets.UTF_8)));
        });
  }
}