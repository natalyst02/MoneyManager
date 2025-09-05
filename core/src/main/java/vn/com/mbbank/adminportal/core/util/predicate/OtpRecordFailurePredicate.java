package vn.com.mbbank.adminportal.core.util.predicate;

import vn.com.mbbank.adminportal.common.exception.BusinessErrorCode;
import vn.com.mbbank.adminportal.common.exception.PaymentPlatformException;

import java.util.Set;
import java.util.function.Predicate;

import static vn.com.mbbank.adminportal.core.util.ErrorCode.SEND_OTP_ERROR;
import static vn.com.mbbank.adminportal.core.util.ErrorCode.SEND_OTP_TIMEOUT;
import static vn.com.mbbank.adminportal.core.util.ErrorCode.VERIFY_OTP_ERROR;
import static vn.com.mbbank.adminportal.core.util.ErrorCode.VERIFY_OTP_TIMEOUT;

public class OtpRecordFailurePredicate implements Predicate<Throwable> {
  private static final Set<BusinessErrorCode> ERROR_CODES = Set.of(
      SEND_OTP_ERROR,
      SEND_OTP_TIMEOUT,
      VERIFY_OTP_TIMEOUT,
      VERIFY_OTP_ERROR
  );

  @Override
  public boolean test(Throwable throwable) {
    if (throwable instanceof PaymentPlatformException ppe) {
      return ERROR_CODES.contains(ppe.getErrorCode());
    }
    return false;
  }
}
