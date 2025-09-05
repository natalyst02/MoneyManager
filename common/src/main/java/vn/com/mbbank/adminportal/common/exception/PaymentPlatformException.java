package vn.com.mbbank.adminportal.common.exception;

import lombok.Getter;

@Getter
public class PaymentPlatformException extends RuntimeException {
  private final transient BusinessErrorCode errorCode;

  public PaymentPlatformException(BusinessErrorCode errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }

  public PaymentPlatformException(BusinessErrorCode errorCode, String message, Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
  }
}
