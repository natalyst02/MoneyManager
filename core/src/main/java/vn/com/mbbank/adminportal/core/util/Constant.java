package vn.com.mbbank.adminportal.core.util;

public class Constant {

  public static final String DEFAULT_ROLE = "PMP_DEFAULT";
  public static final String HCM_PAYMENTPLATFORM_GET_EMP_BY_AD = "HCM_PAYMENTPLATFORM_GET_EMP_BY_AD";
  public static final String PHONE_NUMBER = "PHONE_NUMBER";
  public static final String EMAIL = "EMAIL";
  public static final String USER_AD = "USER_AD";
  public static final String OTP_NOTIFY = "Notify";
  public static final String OTP_SIZE_SIX = "6";
  public static final String ADMIN_PORTAL = "ADMIN_PORTAL:";
  public static final String SEND_OR_VERIFY_OTP_SUCCESS_CODE = "000";
  public static final String CACHE_OTP = "PAP_CACHE_OTP:";
  public static final String RATE_LIMIT_OTP = "PAP_RATE_LIMIT_OTP:";
  public static final String ERROR_CODE = "ERROR_CODE";
  public static final String PAP_USER_KEY = "pap_user:";
  public static final String PAP_VALID_TOKEN = "PAP_VALID_TOKEN:";
  public static final long PAP_VALID_TOKEN_DURATION = 300;

  private Constant() {
    throw new UnsupportedOperationException();
  }
}
