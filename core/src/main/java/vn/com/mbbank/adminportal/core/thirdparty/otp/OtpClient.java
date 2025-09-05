package vn.com.mbbank.adminportal.core.thirdparty.otp;

import vn.com.mbbank.adminportal.core.thirdparty.otp.model.SendOtpRequest;
import vn.com.mbbank.adminportal.core.thirdparty.otp.model.SendOtpResponse;
import vn.com.mbbank.adminportal.core.thirdparty.otp.model.VerifyOtpRequest;

import java.util.concurrent.CompletableFuture;

public interface OtpClient {
  CompletableFuture<SendOtpResponse> sendOtp(SendOtpRequest sendOtpRequest);

  CompletableFuture<Boolean> verifyOtp(VerifyOtpRequest verifyOtpClientRequest);
}
