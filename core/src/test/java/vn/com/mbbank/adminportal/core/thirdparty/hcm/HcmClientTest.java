package vn.com.mbbank.adminportal.core.thirdparty.hcm;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import vn.com.mbbank.adminportal.common.ApplicationTest;
import vn.com.mbbank.adminportal.common.exception.PaymentPlatformException;
import vn.com.mbbank.adminportal.core.thirdparty.hcm.impl.HcmClientImpl;

import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;
import static vn.com.mbbank.adminportal.core.util.ErrorCode.HCM_USER_NOT_FOUND;

public class HcmClientTest extends ApplicationTest {
  @Autowired
  private HcmClientImpl hcmClient;

  @Test
  void getUserInfo_ThenReturnSuccess() {
    var response = hcmClient.getUser("quydv2").join();
    assertNotNull(response);
    assertTrue(response.getEmployeeCode().equals("MB00021396")
        && response.getFullName().equals("Đoàn Văn Quý")
        && response.getMobileNumber().equals("0983591359")
        && response.getEmail().equals("quydv2@dev.mbbank"));
  }

  @Test
  void getUserInfo_ThenThrownException() {
    var exception = assertThrows(CompletionException.class, () -> hcmClient.getUser("fakeaccount").join());
    assertTrue(exception.getCause() instanceof PaymentPlatformException be
        && be.getErrorCode().code().equals(HCM_USER_NOT_FOUND.code())
        && be.getMessage().equals("User: fakeaccount not found"));
  }
}
