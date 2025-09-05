package vn.com.mbbank.adminportal.core.util.predicate;

import vn.com.mbbank.adminportal.common.exception.PaymentPlatformException;
import vn.com.mbbank.adminportal.core.util.ErrorCode;

import java.util.Set;
import java.util.function.Predicate;

public class KeycloakInternalRecordFailurePredicate implements Predicate<Throwable> {

  private static final Set<String> ERROR_CODES = Set.of(
      ErrorCode.ASSIGN_ROLE_ERROR.code(),
      ErrorCode.ASSIGN_ROLE_TIMEOUT.code(),
      ErrorCode.REVOKE_ROLE_ERROR.code(),
      ErrorCode.REVOKE_ROLE_TIMEOUT.code(),
      ErrorCode.GET_KEYCLOAK_USER_INFO_ERROR.code(),
      ErrorCode.GET_KEYCLOAK_USER_INFO_TIMEOUT.code(),
      ErrorCode.VALIDATE_TOKEN_ERROR.code(),
      ErrorCode.VALIDATE_TOKEN_TIMEOUT.code()
  );

  @Override
  public boolean test(Throwable throwable) {
    if (throwable instanceof PaymentPlatformException ppe) {
      return ERROR_CODES.contains(ppe.getErrorCode().code());
    }
    return false;
  }
}
