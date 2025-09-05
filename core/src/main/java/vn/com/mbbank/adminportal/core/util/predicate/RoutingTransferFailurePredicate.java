package vn.com.mbbank.adminportal.core.util.predicate;

import vn.com.mbbank.adminportal.common.exception.BusinessErrorCode;
import vn.com.mbbank.adminportal.common.exception.PaymentPlatformException;

import java.util.Set;
import java.util.function.Predicate;

import static vn.com.mbbank.adminportal.core.util.ErrorCode.GET_BANKS_ERROR;
import static vn.com.mbbank.adminportal.core.util.ErrorCode.GET_BANKS_TIMEOUT;

public class RoutingTransferFailurePredicate implements Predicate<Throwable> {
  private static final Set<BusinessErrorCode> ERROR_CODES = Set.of(
      GET_BANKS_ERROR,
      GET_BANKS_TIMEOUT
  );

  @Override
  public boolean test(Throwable throwable) {
    if (throwable instanceof PaymentPlatformException ppe) {
      return ERROR_CODES.contains(ppe.getErrorCode());
    }
    return false;
  }
}
