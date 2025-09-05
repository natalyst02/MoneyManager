package vn.com.mbbank.adminportal.core.service;

import vn.com.mbbank.adminportal.common.model.MBStaff;
import vn.com.mbbank.adminportal.core.model.request.VerifyOtpRequest;
import vn.com.mbbank.adminportal.core.model.response.SendOtpResponse;
import vn.com.mbbank.adminportal.core.thirdparty.otp.model.VerifyOtpResponse;

import java.util.concurrent.CompletableFuture;

public interface OtpService {
  CompletableFuture<SendOtpResponse> sendOtp(MBStaff mbStaff);

  CompletableFuture<VerifyOtpResponse> verifyOtp(MBStaff mbStaff, VerifyOtpRequest verifyOtpRequest);
}
