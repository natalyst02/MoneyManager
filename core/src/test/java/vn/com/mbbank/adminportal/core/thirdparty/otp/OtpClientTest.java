package vn.com.mbbank.adminportal.core.thirdparty.otp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import vn.com.mbbank.adminportal.common.ApplicationTest;
import vn.com.mbbank.adminportal.common.exception.PaymentPlatformException;
import vn.com.mbbank.adminportal.core.thirdparty.otp.model.OtpAdditionalInfo;
import vn.com.mbbank.adminportal.core.thirdparty.otp.model.SendOtpRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;
import static vn.com.mbbank.adminportal.core.util.Constant.*;
import static vn.com.mbbank.adminportal.core.util.ErrorCode.SEND_OTP_FAIL;

class OtpClientTest extends ApplicationTest {
  @Autowired
  private OtpClient otpClient;

  @Test
  void sendOtp_ThenReturnSuccess() {
    SendOtpRequest sendOtpRequest = new SendOtpRequest();
    List<OtpAdditionalInfo> additionalInfos = new ArrayList<>();
    var additionalInfo1 = new OtpAdditionalInfo();
    var additionalInfo2 = new OtpAdditionalInfo();
    additionalInfo1.setName(PHONE_NUMBER).setValue("012345678");
    additionalInfo2.setName(USER_AD).setValue("username");
    additionalInfos.add(additionalInfo1);
    additionalInfos.add(additionalInfo2);

    sendOtpRequest.setAdditionalInfos(additionalInfos)
        .setOtpType(OTP_NOTIFY)
        .setOtpSize(OTP_SIZE_SIX)
        .setOtpKey(ADMIN_PORTAL + UUID.randomUUID());

    var response = otpClient.sendOtp(sendOtpRequest).join();
    assertNotNull(response);
    assertTrue(response.getErrorCode().equals(SEND_OR_VERIFY_OTP_SUCCESS_CODE)
        && response.getAdditionalInfos().get(0).getName().equals(SEND_OR_VERIFY_OTP_SUCCESS_CODE)
        && response.getOtpTime().equals("120000"));
  }

  @Test
  void sendOtp_ThenThrownSendOtpFailException() {
    SendOtpRequest sendOtpRequest = new SendOtpRequest();
    List<OtpAdditionalInfo> additionalInfos = new ArrayList<>();
    var additionalInfo1 = new OtpAdditionalInfo();
    var additionalInfo2 = new OtpAdditionalInfo();
    additionalInfo1.setName(PHONE_NUMBER).setValue(null);
    additionalInfo2.setName(USER_AD).setValue("username");
    additionalInfos.add(additionalInfo1);
    additionalInfos.add(additionalInfo2);

    sendOtpRequest.setAdditionalInfos(additionalInfos)
        .setOtpType(OTP_NOTIFY)
        .setOtpSize(OTP_SIZE_SIX)
        .setOtpKey(ADMIN_PORTAL + UUID.randomUUID());

    var exception = assertThrows(CompletionException.class, () -> otpClient.sendOtp(sendOtpRequest).join());
    assertTrue(exception.getCause() instanceof PaymentPlatformException be
        && be.getErrorCode().code().equals(SEND_OTP_FAIL.code())
        && be.getMessage().contains("Can not send OTP due otp response:"));
  }
}
