package vn.com.mbbank.adminportal.core.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.com.mbbank.adminportal.common.config.RedisClusterAdapter;
import vn.com.mbbank.adminportal.common.exception.PaymentPlatformException;
import vn.com.mbbank.adminportal.common.model.MBStaff;
import vn.com.mbbank.adminportal.common.service.RateLimiter;
import vn.com.mbbank.adminportal.core.model.response.SendOtpResponse;
import vn.com.mbbank.adminportal.core.service.OtpService;
import vn.com.mbbank.adminportal.core.thirdparty.hcm.HcmClient;
import vn.com.mbbank.adminportal.core.thirdparty.hcm.model.GetHcmUserInfoResponse;
import vn.com.mbbank.adminportal.core.thirdparty.otp.OtpClient;
import vn.com.mbbank.adminportal.core.thirdparty.otp.model.OtpAdditionalInfo;
import vn.com.mbbank.adminportal.core.thirdparty.otp.model.SendOtpRequest;
import vn.com.mbbank.adminportal.core.thirdparty.otp.model.VerifyOtpRequest;
import vn.com.mbbank.adminportal.core.thirdparty.otp.model.VerifyOtpResponse;
import vn.com.mbbank.adminportal.core.util.ErrorCode;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static vn.com.mbbank.adminportal.core.util.Constant.*;

@Service
@Log4j2
public class OtpServiceImpl implements OtpService {
  private final HcmClient hcmClient;
  private final OtpClient otpClient;
  private final RedisClusterAdapter redisClusterAdapter;
  private final RateLimiter rateLimiter;
  private final long verifyLimit;
  private final long verifyTimeSeconds;

  public OtpServiceImpl(HcmClient hcmClient,
                        OtpClient otpClient,
                        RedisClusterAdapter redisClusterAdapter,
                        RateLimiter rateLimiter,
                        @Value("${rate-limit.otp.verify-limit}") long verifyLimit,
                        @Value("${rate-limit.otp.verify-time-seconds}") long verifyTimeSeconds) {
    this.hcmClient = hcmClient;
    this.otpClient = otpClient;
    this.redisClusterAdapter = redisClusterAdapter;
    this.rateLimiter = rateLimiter;
    this.verifyLimit = verifyLimit;
    this.verifyTimeSeconds = verifyTimeSeconds;
  }

  @Override
  public CompletableFuture<SendOtpResponse> sendOtp(MBStaff mbStaff) {
    var username = mbStaff.getUsername();
    return hcmClient.getUser(username)
        .thenCompose(userResponse -> {
          SendOtpRequest sendOtpRequest = createSendOtpRequest(userResponse, username);
          return otpClient.sendOtp(sendOtpRequest).thenApply(response -> {
            SendOtpResponse sendOtpResponse = new SendOtpResponse();
            sendOtpResponse.setOtpKey(sendOtpRequest.getOtpKey())
                .setOtpTime(response.getOtpTime());
            return sendOtpResponse;
          });
        });
  }

  @Override
  public CompletableFuture<VerifyOtpResponse> verifyOtp(MBStaff mbStaff, vn.com.mbbank.adminportal.core.model.request.VerifyOtpRequest verifyOtpRequest) {
    var username = mbStaff.getUsername();
    var sessionState = mbStaff.getSessionState();
    var rateLimitKey = RATE_LIMIT_OTP + username + ":" + sessionState;
    if (!rateLimiter.isAllowed(rateLimitKey, verifyLimit, verifyTimeSeconds)){
      return CompletableFuture.failedFuture(new PaymentPlatformException(ErrorCode.EXCEEDED_OTP_VERIFYING, "Verifying OTP reached limit"));
    }
    var metadata = new VerifyOtpRequest.Metadata().setUserAd(username);
    var verifyOtpClientRequest = new VerifyOtpRequest()
        .setMetadata(metadata)
        .setOtpKey(verifyOtpRequest.getOtpKey().trim())
        .setOtpValue(verifyOtpRequest.getOtpValue().trim())
        .setOtpType(OTP_NOTIFY);
    log.info(() -> "[Verify OTP] verifyOtpClientRequest to 2FA: " + verifyOtpClientRequest);
    return otpClient.verifyOtp(verifyOtpClientRequest)
        .thenApply(response -> {
          redisClusterAdapter.delete(PAP_USER_KEY + mbStaff.getUsername());
          var duration = mbStaff.getTokenExpiredAt() == null
              ? (System.currentTimeMillis() + 1800) : mbStaff.getTokenExpiredAt() - System.currentTimeMillis() / 1000;
          var otpCacheKey = CACHE_OTP + mbStaff.getUsername() + ":" + sessionState;
          redisClusterAdapter.set(otpCacheKey, duration, true);
          return new VerifyOtpResponse().setVerify(response);
        });
  }

  private SendOtpRequest createSendOtpRequest(GetHcmUserInfoResponse userResponse, String username) {
    SendOtpRequest sendOtpRequest = new SendOtpRequest();
    List<OtpAdditionalInfo> additionalInfos = new ArrayList<>();
    var additionalInfo1 = new OtpAdditionalInfo();
    var additionalInfo2 = new OtpAdditionalInfo();
    var additionalInfo3 = new OtpAdditionalInfo();

    additionalInfo1.setName(PHONE_NUMBER).setValue(userResponse.getMobileNumber());
    additionalInfo2.setName(EMAIL);
    additionalInfo3.setName(USER_AD).setValue(username);
    additionalInfos.add(additionalInfo1);
    additionalInfos.add(additionalInfo2);
    additionalInfos.add(additionalInfo3);

    sendOtpRequest.setAdditionalInfos(additionalInfos)
        .setOtpType(OTP_NOTIFY)
        .setOtpSize(OTP_SIZE_SIX)
        .setOtpKey(ADMIN_PORTAL + UUID.randomUUID());

    return sendOtpRequest;
  }
}
