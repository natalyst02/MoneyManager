package vn.com.mbbank.adminportal.common.util;

public class Constant {
  public static final String PREFIX_RESPONSE_CODE = System.getProperty("service.response.prefix-code", "PAP-");
  public static final String ORIGINAL_SERVICE = System.getProperty("service.code");
  public static final String SYSTEM_USER = "system";
  public static final String CLIENT_MESSAGE_ID = "clientMessageId";
  public static final String PATH = "path";
  public static final String OAUTH_GRANT_TYPE = "client_credentials";
  public static final String X_AUTH_USER = "X_AUTH_USER";
  public static final String TRANSACTION_ID = "transactionId";
  public static final long REDIS_DEFAULT_DURATION = 3600;

  public static final String PAP_USER_KEY = "pap_user:";

  private Constant() {
    throw new UnsupportedOperationException();
  }
}
