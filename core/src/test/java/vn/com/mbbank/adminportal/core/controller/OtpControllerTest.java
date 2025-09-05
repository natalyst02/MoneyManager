package vn.com.mbbank.adminportal.core.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import vn.com.mbbank.adminportal.common.ApplicationTest;
import vn.com.mbbank.adminportal.common.WithMockMBStaff;
import vn.com.mbbank.adminportal.common.config.RedisClusterAdapter;
import vn.com.mbbank.adminportal.common.exception.NSTCompletionException;
import vn.com.mbbank.adminportal.common.exception.PaymentPlatformException;
import vn.com.mbbank.adminportal.common.util.Constant;
import vn.com.mbbank.adminportal.core.thirdparty.otp.OtpClient;
import vn.com.mbbank.adminportal.core.thirdparty.otp.model.SendOtpResponse;

import java.util.concurrent.CompletableFuture;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static vn.com.mbbank.adminportal.common.util.CommonErrorCode.UNAUTHORIZED;
import static vn.com.mbbank.adminportal.core.util.Constant.RATE_LIMIT_OTP;
import static vn.com.mbbank.adminportal.core.util.ErrorCode.HCM_USER_NOT_FOUND;
import static vn.com.mbbank.adminportal.core.util.ErrorCode.VERIFY_OTP_ERROR;

class OtpControllerTest extends ApplicationTest {
  @Autowired
  private MockMvc mvc;

  @MockBean
  OtpClient otpClient;

  @Autowired
  RedisClusterAdapter redisClusterAdapter;

  @BeforeEach
  public void setUp() {
    redisClusterAdapter.delete(RATE_LIMIT_OTP + "quydv2" + ":" + null);
    redisClusterAdapter.delete(RATE_LIMIT_OTP + "thombt" + ":" + null);
    redisClusterAdapter.delete(RATE_LIMIT_OTP + "fakeuser" + ":" + null);
  }

  @WithMockMBStaff(username = "quydv2")
  @Test
  void sendOtp_ThenReturnSuccess() throws Exception {
    SendOtpResponse sendOtpResponse = new SendOtpResponse();
    sendOtpResponse.setOtpTime("120000");
    when(otpClient.sendOtp(any())).thenReturn(CompletableFuture.completedFuture(sendOtpResponse));

    var request = MockMvcRequestBuilders.post("/otp/send");
    var mvcResult = mvc.perform(request)
        .andExpect(MockMvcResultMatchers.request().asyncStarted())
        .andReturn();
    mvc.perform(MockMvcRequestBuilders.asyncDispatch(mvcResult))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status", equalTo(HttpStatus.OK.value())))
        .andExpect(jsonPath("$.error", equalTo(HttpStatus.OK.getReasonPhrase())))
        .andExpect(jsonPath("$.path", equalTo("/otp/send")))
        .andExpect(jsonPath("$.timestamp").isNotEmpty())
        .andExpect(jsonPath("$.data.otpKey").isNotEmpty())
        .andExpect(jsonPath("$.data.otpTime", equalTo("120000")));
  }

  @Test
  void sendOtp_ThenThrownUnauthorizedException() throws Exception {
    var request = MockMvcRequestBuilders.post("/otp/send");
    mvc.perform(request)
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.soaErrorCode", equalTo(Constant.PREFIX_RESPONSE_CODE + UNAUTHORIZED.code())));
  }

  @WithMockMBStaff(username = "fakeuser")
  @Test
  void sendOtp_ThenReturnFail() throws Exception {
    var request = MockMvcRequestBuilders.post("/otp/send");
    var mvcResult = mvc.perform(request)
        .andExpect(MockMvcResultMatchers.request().asyncStarted())
        .andReturn();
    mvc.perform(MockMvcRequestBuilders.asyncDispatch(mvcResult))
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.status", equalTo(HttpStatus.INTERNAL_SERVER_ERROR.value())))
        .andExpect(jsonPath("$.error", equalTo(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())))
        .andExpect(jsonPath("$.path", equalTo("/otp/send")))
        .andExpect(jsonPath("$.soaErrorCode", equalTo(Constant.PREFIX_RESPONSE_CODE + HCM_USER_NOT_FOUND.code())))
        .andExpect(jsonPath("$.soaErrorDesc", equalTo("User: fakeuser not found")));
  }

  @WithMockMBStaff(username = "thombt")
  @Test
  void verifyOtp_ThenReturnSuccess() throws Exception {
    when(otpClient.verifyOtp(any())).thenReturn(CompletableFuture.completedFuture(Boolean.TRUE));

    var request = MockMvcRequestBuilders.post("/otp/verify")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
            "otpKey": "ADMIN_PORTAL:2bc471a4-fca5-4732-9dfc-aed80a8787zz",
            "otpValue": "549022"
            }
            """);

    var mvcResult = mvc.perform(request)
        .andExpect(MockMvcResultMatchers.request().asyncStarted())
        .andReturn();

    mvc.perform(MockMvcRequestBuilders.asyncDispatch(mvcResult))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status", equalTo(HttpStatus.OK.value())))
        .andExpect(jsonPath("$.error", equalTo(HttpStatus.OK.getReasonPhrase())))
        .andExpect(jsonPath("$.data.verify").value(true));
  }

  @WithMockMBStaff(username = "quydv2")
  @Test
  void verifyOtp_ThenThrownVerifyErrorException() throws Exception {
    when(otpClient.verifyOtp(any())).thenThrow(new NSTCompletionException(new PaymentPlatformException(VERIFY_OTP_ERROR,
        "Can not verify otp due: ")));
    var request = MockMvcRequestBuilders.post("/otp/verify")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
            "otpKey": "ADMIN_PORTAL:2bc471a4-fca5-4732-9dfc-aed80a8787zz",
            "otpValue": "549022"
            }
            """);
    mvc.perform(request)
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.status", equalTo(HttpStatus.INTERNAL_SERVER_ERROR.value())))
        .andExpect(jsonPath("$.soaErrorCode", equalTo(Constant.PREFIX_RESPONSE_CODE + VERIFY_OTP_ERROR.code())));
  }

  @WithMockMBStaff(username = "thombt")
  @Test
  void verifyOtp_ThenThrownValidatorException() throws Exception {
    var request = MockMvcRequestBuilders.post("/otp/verify")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
            "otpKey": null,
            "otpValue": "54902"
            }
            """);
    mvc.perform(request)
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status", equalTo(HttpStatus.BAD_REQUEST.value())))
        .andExpect(jsonPath("$.soaErrorCode", equalTo("008006-4000")))
        .andExpect(jsonPath("$.soaErrorDesc", equalTo("Invalid parameters")))
        .andExpect(jsonPath("$.errors", hasSize(2)))
        .andExpect(jsonPath("$.errors[*].field", containsInAnyOrder("otpValue", "otpKey")))
        .andExpect(jsonPath("$.errors[*].description", containsInAnyOrder("otpKey is invalid", "otpValue must be exactly 6 characters")));
  }

  @Test
  void verifyOtp_ThenThrownUnauthorizedException() throws Exception {
    var request = MockMvcRequestBuilders.post("/otp/verify")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
            "otpKey": "ADMIN_PORTAL:2bc471a4-fca5-4732-9dfc-aed80a8787zz",
            "otpValue": "549022"
            }
            """);
    mvc.perform(request)
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.status", equalTo(HttpStatus.UNAUTHORIZED.value())))
        .andExpect(jsonPath("$.soaErrorCode", equalTo(Constant.PREFIX_RESPONSE_CODE + UNAUTHORIZED.code())))
        .andExpect(jsonPath("$.soaErrorDesc", equalTo("Full authentication is required to access this resource")));
  }
}
