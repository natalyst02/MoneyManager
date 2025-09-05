package vn.com.mbbank.adminportal.core.thirdparty.routingtransfer.impl;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.runtime.Generics;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import vn.com.mbbank.adminportal.common.exception.NSTCompletionException;
import vn.com.mbbank.adminportal.common.exception.PaymentPlatformException;
import vn.com.mbbank.adminportal.common.model.response.PageImpl;
import vn.com.mbbank.adminportal.common.model.response.Response;
import vn.com.mbbank.adminportal.common.oauth.OAuthClient;
import vn.com.mbbank.adminportal.common.util.Constant;
import vn.com.mbbank.adminportal.common.util.Json;
import vn.com.mbbank.adminportal.common.util.RestApiHelper;
import vn.com.mbbank.adminportal.common.util.RestClient;
import vn.com.mbbank.adminportal.core.model.request.RoutingBankRequest;
import vn.com.mbbank.adminportal.core.thirdparty.routingtransfer.RoutingTransferClient;
import vn.com.mbbank.adminportal.core.thirdparty.routingtransfer.model.response.GetBanksResponse;
import vn.com.mbbank.adminportal.core.util.ErrorCode;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class RoutingTransferClientImpl implements RoutingTransferClient {
  private final static JsonReader.ReadObject<Response<PageImpl<GetBanksResponse>>> getBanksRespReader =
      Json.findReader(Generics.makeParameterizedType(Response.class,
          Generics.makeParameterizedType(PageImpl.class, GetBanksResponse.class)));
  private final RestClient restClient;
  private final OAuthClient oAuthClient;
  private final URI getBanksUri;

  public RoutingTransferClientImpl(RestClient restClient, String oauthUrl,
                                   String clientId,
                                   String clientSecret, String url) {
    this.restClient = restClient;
    this.oAuthClient = OAuthClient
        .builder()
        .setRestClient(restClient)
        .setGrantType(Constant.OAUTH_GRANT_TYPE)
        .setTokenApiUrl(URI.create(oauthUrl))
        .setClientId(clientId)
        .setClientSecret(clientSecret)
        .setUseCache(true)
        .build();
    this.getBanksUri = URI.create(url + "/banks");
  }

  @CircuitBreaker(name = "getBanks", fallbackMethod = "getBanksAsyncFallback")
  @Override
  public CompletableFuture<PageImpl<GetBanksResponse>> getBanksAsync(RoutingBankRequest request, Pageable pageable) {
    return oAuthClient.getAccessToken()
        .thenCompose(accessToken -> {
          var clientMessageId = RestApiHelper.getOrCreateClientMessageId();
          var headers = Map.of(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE,
              Constant.CLIENT_MESSAGE_ID, clientMessageId,
              HttpHeaders.AUTHORIZATION, "Bearer " + accessToken.getToken());
          var showBranch = request.getShowBranch() != null ? request.getShowBranch() : true;
          return restClient.get(URI.create(getBanksUri + "?page=" + pageable.getPageNumber() + "&size=" + pageable.getPageSize()
              + "&showBranch=" + showBranch + "&sort=" + URLEncoder.encode(pageable.getSort().toString().replace(" ", ""), StandardCharsets.UTF_8)
              + "&bankCode=" + (request.getBankCode() != null ? request.getBankCode() : "")), headers);
        })
        .handle(((httpResponse, throwable) -> {
          if (throwable != null) {
            RestApiHelper.handleException(throwable,
                ErrorCode.GET_BANKS_TIMEOUT, () -> "Get banks timeout",
                ErrorCode.GET_BANKS_ERROR, () -> "Get banks error");
          }
          var respBody = Json.decode(httpResponse.body(), getBanksRespReader, e -> new NSTCompletionException(new PaymentPlatformException(ErrorCode.GET_BANKS_ERROR,
              "Can't get banks due: " + new String(httpResponse.body(), StandardCharsets.UTF_8), e)));
          var statusCode = httpResponse.statusCode();
          if (statusCode == 200) {
            return respBody.getData();
          } else {
            throw new NSTCompletionException(new PaymentPlatformException(ErrorCode.GET_BANKS_ERROR,
                "Can't get banks due : " + new String(httpResponse.body(), StandardCharsets.UTF_8)));
          }
        }));
  }

  public CompletableFuture<PageImpl<GetBanksResponse>> getBanksAsyncFallback(RoutingBankRequest request, Pageable pageable, CallNotPermittedException e) {
    return CompletableFuture.failedFuture(new PaymentPlatformException(ErrorCode.GET_BANKS_ERROR,
        "Get banks error due: circuit breaker was opened", e));
  }
}
