package vn.com.mbbank.adminportal.core.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import vn.com.mbbank.adminportal.common.ApplicationTest;
import vn.com.mbbank.adminportal.common.WithMockMBStaff;
import vn.com.mbbank.adminportal.common.config.RedisClusterAdapter;
import vn.com.mbbank.adminportal.common.exception.PaymentPlatformException;
import vn.com.mbbank.adminportal.common.model.MBStaff;
import vn.com.mbbank.adminportal.common.util.Authentications;
import vn.com.mbbank.adminportal.core.model.response.SendOtpResponse;
import vn.com.mbbank.adminportal.core.thirdparty.hcm.HcmClient;
import vn.com.mbbank.adminportal.core.thirdparty.hcm.model.GetHcmUserInfoResponse;
import vn.com.mbbank.adminportal.core.thirdparty.otp.OtpClient;
import vn.com.mbbank.adminportal.core.thirdparty.otp.model.VerifyOtpRequest;
import vn.com.mbbank.adminportal.core.thirdparty.otp.model.VerifyOtpResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static vn.com.mbbank.adminportal.core.util.Constant.*;
import static vn.com.mbbank.adminportal.core.util.ErrorCode.*;

class OtpServiceImplTest extends ApplicationTest {
  @Autowired
  OtpService otpService;

  @SpyBean
  HcmClient hcmClient;

  private MBStaff principal;

  @SpyBean
  OtpClient otpClient;

  @SpyBean
  RedisClusterAdapter redisClusterAdapter;

  private Authentication authentication;

  @BeforeEach
  public void setUp() {
    principal = new MBStaff().setUsername("thombt").setSessionState("session1");
    authentication = new UsernamePasswordAuthenticationToken(principal, null, List.of());
    var rateLimitKey = RATE_LIMIT_OTP + principal.getUsername() + ":" + principal.getSessionState();
    redisClusterAdapter.delete(rateLimitKey);
  }

  @Test
  @WithMockMBStaff(username = "quydv2")
  void sendOtp_ThenReturnSuccess() {
    CompletableFuture<SendOtpResponse> otp = otpService.sendOtp(Authentications.requireMBStaff());
    SendOtpResponse sendOtpResponse = otp.join();
    assertNotNull(sendOtpResponse);
    assertNotNull(sendOtpResponse.getOtpKey());
    assertEquals("120000", sendOtpResponse.getOtpTime());
  }

  @Test
  @WithMockMBStaff(username = "thombt")
  void sendOtp_ThenThrownSendOtpFailException() {
    var username = "thombt";
    var mockUserResponse = mock(GetHcmUserInfoResponse.class);
    when(hcmClient.getUser(username)).thenReturn(CompletableFuture.completedFuture(mockUserResponse));
    when(mockUserResponse.getMobileNumber()).thenReturn(null);
    var exception = assertThrows(CompletionException.class, () -> otpService.sendOtp(Authentications.requireMBStaff()).join());
    assertTrue(exception.getCause() instanceof PaymentPlatformException be
        && be.getErrorCode().code().equals(SEND_OTP_FAIL.code())
        && be.getMessage().contains("Can not send OTP due otp response: "));
  }

  @Test
  @WithMockMBStaff(username = "thombt")
  void verifyOtp_ThenReturnSuccess() {
    var verifyOtpRequest = new vn.com.mbbank.adminportal.core.model.request.VerifyOtpRequest("ADMIN_PORTAL", "123456");
    var mbStaff = Authentications.requireMBStaff(authentication);
    var username = mbStaff.getUsername();
    var metadata = new VerifyOtpRequest.Metadata().setUserAd(username);
    var verifyOtpClientRequest = new VerifyOtpRequest().setMetadata(metadata)
        .setOtpKey(verifyOtpRequest.getOtpKey().trim())
        .setOtpValue(verifyOtpRequest.getOtpValue().trim())
        .setOtpType(OTP_NOTIFY);
    VerifyOtpResponse mockVerifyOtpResponse = mock(VerifyOtpResponse.class);
    when(mockVerifyOtpResponse.isVerify()).thenReturn(true);
    when(otpClient.verifyOtp(verifyOtpClientRequest)).thenReturn(CompletableFuture.completedFuture(Boolean.TRUE));
    assertTrue(otpService.verifyOtp(mbStaff, verifyOtpRequest).join().isVerify());
    assertTrue(redisClusterAdapter.get(username, Boolean.class));
  }

  @Test
  @WithMockMBStaff(username = "thombt")
  void verifyOtp_ThenThrownVerifyOtpException() {
    var verifyOtpRequest = new vn.com.mbbank.adminportal.core.model.request.VerifyOtpRequest("ADMIN_PORTAL", "abcdef");
    var mbStaff = Authentications.requireMBStaff(authentication);
    var exception = assertThrows(CompletionException.class, () -> otpService.verifyOtp(mbStaff, verifyOtpRequest).join());
    assertTrue(exception.getCause() instanceof PaymentPlatformException be
        && be.getErrorCode().code().equals(VERIFY_OTP_ERROR.code())
        && be.getMessage().contains("Can not verify otp due: "));
  }

  @Test
  @WithMockMBStaff(username = "thombt")
  void verifyOtp_ReachLimit() {
    var verifyOtpRequest = new vn.com.mbbank.adminportal.core.model.request.VerifyOtpRequest("ADMIN_PORTAL", "123456");
    Authentications.requireMBStaff(authentication);
    for (int i = 0; i < 3; ++i) {
      try {
        otpService.verifyOtp(Authentications.requireMBStaff(), verifyOtpRequest).join();
      } catch (Exception e) {
        //skip error
      }
    }
    var exception = assertThrows(CompletionException.class, () -> otpService.verifyOtp(Authentications.requireMBStaff(), verifyOtpRequest).join());
    assertTrue(exception.getCause() instanceof PaymentPlatformException);
    var paymentPlatformException = (PaymentPlatformException) exception.getCause();
    assertEquals(paymentPlatformException.getErrorCode().code(), EXCEEDED_OTP_VERIFYING.code());
  }
}
