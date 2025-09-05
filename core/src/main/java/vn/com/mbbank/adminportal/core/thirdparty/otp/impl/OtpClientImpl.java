package vn.com.mbbank.adminportal.core.thirdparty.otp.impl;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.runtime.Generics;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.com.mbbank.adminportal.common.exception.NSTCompletionException;
import vn.com.mbbank.adminportal.common.exception.PaymentPlatformException;
import vn.com.mbbank.adminportal.common.model.response.Response;
import vn.com.mbbank.adminportal.common.oauth.OAuthClient;
import vn.com.mbbank.adminportal.common.util.Constant;
import vn.com.mbbank.adminportal.common.util.Json;
import vn.com.mbbank.adminportal.common.util.RestApiHelper;
import vn.com.mbbank.adminportal.common.util.RestClient;
import vn.com.mbbank.adminportal.core.thirdparty.otp.OtpClient;
import vn.com.mbbank.adminportal.core.thirdparty.otp.model.OtpAdditionalInfo;
import vn.com.mbbank.adminportal.core.thirdparty.otp.model.SendOtpRequest;
import vn.com.mbbank.adminportal.core.thirdparty.otp.model.SendOtpResponse;
import vn.com.mbbank.adminportal.core.thirdparty.otp.model.VerifyOtpRequest;
import vn.com.mbbank.adminportal.core.thirdparty.otp.model.VerifyOtpResponse;
import vn.com.mbbank.adminportal.core.util.RequestHelper;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static vn.com.mbbank.adminportal.core.util.Constant.SEND_OR_VERIFY_OTP_SUCCESS_CODE;
import static vn.com.mbbank.adminportal.core.util.Constant.USER_AD;
import static vn.com.mbbank.adminportal.core.util.ErrorCode.SEND_OTP_ERROR;
import static vn.com.mbbank.adminportal.core.util.ErrorCode.SEND_OTP_FAIL;
import static vn.com.mbbank.adminportal.core.util.ErrorCode.SEND_OTP_TIMEOUT;
import static vn.com.mbbank.adminportal.core.util.ErrorCode.VERIFY_OTP_ERROR;
import static vn.com.mbbank.adminportal.core.util.ErrorCode.VERIFY_OTP_TIMEOUT;

@Service
@Log4j2
public class OtpClientImpl implements OtpClient {
  private final RestClient restClient;
  private final OAuthClient oAuthClient;
  private final String baseUrl;

  private final static JsonReader.ReadObject<Response<SendOtpResponse>> sendOtpRespReader =
      Json.findReader(Generics.makeParameterizedType(Response.class, SendOtpResponse.class));

  private final static JsonReader.ReadObject<Response> verifyOtpRespReader = Json.findReader(Response.class);

  public OtpClientImpl(RestClient restClient,
                       @Value("${otp.url}") String url,
                       @Value("${oauth2.url}") String oauth2Url,
                       @Value("${otp.client-id}") String clientId,
                       @Value("${otp.client-secret}") String clientSecret) {
    this.restClient = restClient;
    this.oAuthClient = OAuthClient.builder()
        .setRestClient(restClient)
        .setClientId(clientId)
        .setClientSecret(clientSecret)
        .setTokenApiUrl(URI.create(oauth2Url))
        .setGrantType(Constant.OAUTH_GRANT_TYPE)
        .setUseCache(true)
        .build();
    this.baseUrl = url;
  }

  @Override
  @CircuitBreaker(name = "sendOtp", fallbackMethod = "sendOtpFallback")
  public CompletableFuture<SendOtpResponse> sendOtp(SendOtpRequest sendOtpRequest) {
    var clientMessageId = RestApiHelper.getOrCreateClientMessageId();
    List<OtpAdditionalInfo> additionalInfos = sendOtpRequest.getAdditionalInfos();
    String user = additionalInfos.stream().filter(info -> USER_AD.equals(info.getName())).map(OtpAdditionalInfo::getValue).findFirst().get();
    return oAuthClient.getAccessToken()
        .thenCompose(accessToken -> {
          var header = RequestHelper.createJsonHeader(clientMessageId, accessToken);
          return restClient.post(URI.create(baseUrl), header, sendOtpRequest);
        })
        .handle(((httpResponse, throwable) -> {
          if (throwable != null) {
            RestApiHelper.handleException(throwable,
                SEND_OTP_TIMEOUT, () -> "Sent otp to user: " + user + " timeout",
                SEND_OTP_ERROR, () -> "Sent otp to user: " + user + " error");
          }

          var sendOtpResponse = Json.decode(httpResponse.body(), sendOtpRespReader,
              e -> new NSTCompletionException(
                  new PaymentPlatformException(SEND_OTP_ERROR,
                      "Can not send otp due: " + new String(httpResponse.body(), StandardCharsets.UTF_8))));
          var statusCode = httpResponse.statusCode();
          if (statusCode == 200) {
            var otpResponse = sendOtpResponse.getData();
            if (otpResponse == null || otpResponse.getAdditionalInfos().isEmpty()
                || !otpResponse.getAdditionalInfos().get(0).getName().equals(SEND_OR_VERIFY_OTP_SUCCESS_CODE)) {
              throw new NSTCompletionException(new PaymentPlatformException(SEND_OTP_FAIL,
                  "Can not send OTP due otp response: " + new String(httpResponse.body(), StandardCharsets.UTF_8)));
            }
            return otpResponse;
          } else {
            throw new NSTCompletionException(new PaymentPlatformException(SEND_OTP_ERROR,
                "Can not send otp due: " + new String(httpResponse.body(), StandardCharsets.UTF_8)));
          }
        }));
  }

  @Override
  @CircuitBreaker(name = "verifyOtp", fallbackMethod = "verifyOtpFallback")
  public CompletableFuture<Boolean> verifyOtp(VerifyOtpRequest verifyOtpClientRequest) {
    var clientMessageId = RestApiHelper.getOrCreateClientMessageId();
    var user = verifyOtpClientRequest.getMetadata().getUserAd();
    return oAuthClient.getAccessToken()
        .thenCompose(accessToken -> {
          var header = RequestHelper.createJsonHeader(clientMessageId, accessToken);
          return restClient.post(URI.create(baseUrl + "/verify"), header, verifyOtpClientRequest);
        })
        .handle(((httpResponse, throwable) -> {
          if (throwable != null) {
            RestApiHelper.handleException(throwable,
                VERIFY_OTP_TIMEOUT, () -> "Verify otp from user: " + user + " timeout",
                VERIFY_OTP_ERROR, () -> "Verify otp from user: " + user + " error");
          }

          var verifyOtpResponse = Json.decode(httpResponse.body(), verifyOtpRespReader,
              e -> new NSTCompletionException(
                  new PaymentPlatformException(VERIFY_OTP_ERROR,
                      "Can not verify otp due: " + new String(httpResponse.body(), StandardCharsets.UTF_8))));
          var statusCode = httpResponse.statusCode();
          if (statusCode == 200 && SEND_OR_VERIFY_OTP_SUCCESS_CODE.equals(verifyOtpResponse.getSoaErrorCode())) {
            return Boolean.TRUE;
          } else {
            throw new NSTCompletionException(new PaymentPlatformException(VERIFY_OTP_ERROR,
                "Can not verify otp due: " + new String(httpResponse.body(), StandardCharsets.UTF_8)));
          }
        }));
  }


  public CompletableFuture<SendOtpResponse> sendOtpFallback(SendOtpRequest sendOtpRequest, CallNotPermittedException e) {
    log.error("sendOtpFallback: Send otp with otpKey {} error", sendOtpRequest.getOtpKey(), e);
    return CompletableFuture.failedFuture(new PaymentPlatformException(SEND_OTP_ERROR, SEND_OTP_ERROR.message(), e));
  }

  public CompletableFuture<VerifyOtpResponse> verifyOtpFallback(VerifyOtpRequest verifyOtpClientRequest, CallNotPermittedException e) {
    log.error("verifyOtpFallback: Verify otp with otpKey {} error", verifyOtpClientRequest.getOtpKey(), e);
    return CompletableFuture.failedFuture(new PaymentPlatformException(VERIFY_OTP_ERROR, VERIFY_OTP_ERROR.message(), e));
  }
}
