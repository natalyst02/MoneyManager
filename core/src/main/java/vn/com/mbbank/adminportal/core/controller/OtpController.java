package vn.com.mbbank.adminportal.core.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.com.mbbank.adminportal.common.model.response.Response;
import vn.com.mbbank.adminportal.common.util.Authentications;
import vn.com.mbbank.adminportal.core.model.request.VerifyOtpRequest;
import vn.com.mbbank.adminportal.core.model.response.SendOtpResponse;
import vn.com.mbbank.adminportal.core.service.OtpService;
import vn.com.mbbank.adminportal.core.thirdparty.otp.model.VerifyOtpResponse;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/otp")
@RequiredArgsConstructor
@Validated
public class OtpController {
  private final OtpService otpService;

  @PostMapping("/send")
  public CompletableFuture<Response<SendOtpResponse>> sendOtp() {
    return otpService.sendOtp(Authentications.requireMBStaff()).thenApply(Response::ofSucceeded);
  }

  @PostMapping("/verify")
  public CompletableFuture<Response<VerifyOtpResponse>> verifyOtp(@Valid @RequestBody VerifyOtpRequest verifyOtpRequest) {
    return otpService.verifyOtp(Authentications.requireMBStaff(), verifyOtpRequest).thenApply(Response::ofSucceeded);
  }
}
