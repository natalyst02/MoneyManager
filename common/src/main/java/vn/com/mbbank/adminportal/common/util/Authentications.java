package vn.com.mbbank.adminportal.common.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import vn.com.mbbank.adminportal.common.exception.PaymentPlatformException;
import vn.com.mbbank.adminportal.common.jws.JWSInput;
import vn.com.mbbank.adminportal.common.model.MBStaff;
import vn.com.mbbank.adminportal.common.model.PapUser;
import vn.com.mbbank.adminportal.common.model.User;

import java.util.Optional;

public class Authentications {
  private Authentications() {
  }

  public static String requireUsername() {
    return requireUsername(SecurityContextHolder.getContext().getAuthentication());
  }

  public static String requireUsername(Authentication authentication) {
    return requireMBStaff(authentication).getUsername();
  }

  public static MBStaff requireMBStaff() {
    return requireMBStaff(SecurityContextHolder.getContext().getAuthentication());
  }

  public static MBStaff requireMBStaff(Authentication authentication) {
    if (authentication != null && authentication.getPrincipal() instanceof PapUser papUser) {
      throw new PaymentPlatformException(CommonErrorCode.FORBIDDEN, "You already login 2fa otp with user: " + papUser.getUsername());
    }
    if (authentication != null && authentication.getPrincipal() instanceof MBStaff staff) {
      return staff;
    }
    throw new PaymentPlatformException(CommonErrorCode.FORBIDDEN, "You don't have permission to access this resource.");
  }

  public static Optional<MBStaff> getMBStaff() {
    return getMBStaff(SecurityContextHolder.getContext().getAuthentication());
  }

  public static Optional<MBStaff> getMBStaff(Authentication authentication) {
    if (authentication != null && authentication.getPrincipal() instanceof MBStaff staff) {
      return Optional.of(staff);
    }
    return Optional.empty();
  }

  public static Optional<String> getUsername() {
    return getUsername(SecurityContextHolder.getContext().getAuthentication());
  }

  public static Optional<String> getUsername(Authentication authentication) {
    if (authentication != null && authentication.getPrincipal() instanceof User user) {
      return Optional.of(user.getUsername());
    }
    return Optional.empty();
  }

  public static Optional<String> getToken(Authentication authentication) {
    if (authentication != null && authentication.getCredentials() instanceof JWSInput input) {
      return Optional.of(input.getWireString());
    }
    return Optional.empty();
  }
}
