package vn.com.mbbank.adminportal.core.thirdparty.hcm.impl;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.runtime.Generics;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.com.mbbank.adminportal.common.exception.NSTCompletionException;
import vn.com.mbbank.adminportal.common.exception.PaymentPlatformException;
import vn.com.mbbank.adminportal.common.exception.ServiceException;
import vn.com.mbbank.adminportal.common.model.response.Response;
import vn.com.mbbank.adminportal.common.oauth.OAuthClient;
import vn.com.mbbank.adminportal.common.util.Constant;
import vn.com.mbbank.adminportal.common.util.Json;
import vn.com.mbbank.adminportal.common.util.RestApiHelper;
import vn.com.mbbank.adminportal.common.util.RestClient;
import vn.com.mbbank.adminportal.core.thirdparty.hcm.HcmClient;
import vn.com.mbbank.adminportal.core.thirdparty.hcm.model.GetHcmUserInfoResponse;
import vn.com.mbbank.adminportal.core.util.ErrorCode;
import vn.com.mbbank.adminportal.core.util.RequestHelper;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

import static vn.com.mbbank.adminportal.core.util.Constant.HCM_PAYMENTPLATFORM_GET_EMP_BY_AD;
import static vn.com.mbbank.adminportal.core.util.ErrorCode.HCM_USER_PHONE_NUMBER_NULL;

@Service
@Log4j2
public class HcmClientImpl implements HcmClient {
  private final RestClient restClient;
  private final OAuthClient oAuthClient;
  private final String baseUrl;

  private final static JsonReader.ReadObject<Response<GetHcmUserInfoResponse>> getUserInfoRespReader =
      Json.findReader(Generics.makeParameterizedType(Response.class, GetHcmUserInfoResponse.class));

  public HcmClientImpl(RestClient restClient,
                       @Value("${hcm.url}") String url,
                       @Value("${keycloak.internal.url}") String oauth2Url,
                       @Value("${hcm.client-id}") String clientId,
                       @Value("${hcm.client-secret}") String clientSecret) {
    this.restClient = restClient;
    this.oAuthClient = OAuthClient.builder()
        .setRestClient(restClient)
        .setClientId(clientId)
        .setClientSecret(clientSecret)
        .setTokenApiUrl(URI.create(oauth2Url + "/realms/internal/protocol/openid-connect/token"))
        .setGrantType(Constant.OAUTH_GRANT_TYPE)
        .setUseCache(true)
        .build();
    this.baseUrl = url + "/hcm-itg/v1.0/get-data";
  }

  @Override
  @CircuitBreaker(name = "getHcmUserInfo", fallbackMethod = "getHcmUserFallback")
  public CompletableFuture<GetHcmUserInfoResponse> getUser(String username) {
    var urlGetInfo = baseUrl + "?codeSystem=" + HCM_PAYMENTPLATFORM_GET_EMP_BY_AD + "&username=" + username;
    var clientMessageId = RestApiHelper.getOrCreateClientMessageId();

    return oAuthClient.getAccessToken()
        .thenCompose(accessToken -> {
          var header = RequestHelper.createJsonHeader(clientMessageId, accessToken);
          return restClient.get(URI.create(urlGetInfo), header);
        })
        .handle(((httpResponse, throwable) -> {
          if (throwable != null) {
            RestApiHelper.handleException(throwable,
                ErrorCode.GET_HCM_USER_TIMEOUT, () -> "Get hcm user: " + username + " timeout",
                ErrorCode.GET_HCM_USER_ERROR, () -> "Get hcm user: " + username + " error");
          }

          var respBody = Json.decode(httpResponse.body(), getUserInfoRespReader,
              e -> new NSTCompletionException(
                  new PaymentPlatformException(ErrorCode.GET_HCM_USER_ERROR,
                      "Can not get user info due: " + new String(httpResponse.body(), StandardCharsets.UTF_8))));
          var statusCode = httpResponse.statusCode();
          if (statusCode == 200) {
            if (respBody.getData() == null) {
              throw new NSTCompletionException(new PaymentPlatformException(ErrorCode.HCM_USER_NOT_FOUND, "User: " + username + " not found"));
            }
            if (StringUtils.isBlank(respBody.getData().getMobileNumber())) {
              throw new NSTCompletionException(new PaymentPlatformException(HCM_USER_PHONE_NUMBER_NULL, "User: " + username + " don't have phone number"));
            }
            return respBody.getData();
          } else {
            throw new NSTCompletionException(new PaymentPlatformException(ErrorCode.GET_HCM_USER_ERROR,
                "Can not get user info with username: " + username + " due: " + new String(httpResponse.body(), StandardCharsets.UTF_8)));
          }
        }));
  }

  public CompletableFuture<GetHcmUserInfoResponse> getHcmUserFallback(String username, CallNotPermittedException e) {
    log.error("Hcm GetUserInfoFallback: Get user info with username {} error", username, e);
    return CompletableFuture.failedFuture(new PaymentPlatformException(ErrorCode.GET_HCM_USER_ERROR, ErrorCode.GET_HCM_USER_ERROR.message(), e));
  }
}
