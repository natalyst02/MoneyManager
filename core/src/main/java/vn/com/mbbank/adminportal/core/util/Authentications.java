package vn.com.mbbank.adminportal.core.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import vn.com.mbbank.adminportal.common.exception.PaymentPlatformException;
import vn.com.mbbank.adminportal.common.jws.JWSInput;
import vn.com.mbbank.adminportal.common.model.PapUser;
import vn.com.mbbank.adminportal.common.util.CommonErrorCode;

import java.util.Optional;

public class Authentications {
  private Authentications() {
  }

  public static String requireUsername() {
    return requireUsername(SecurityContextHolder.getContext().getAuthentication());
  }

  public static String requireUsername(Authentication authentication) {
    return requirePapUser(authentication).getUsername();
  }

  public static PapUser requirePapUser() {
    return requirePapUser(SecurityContextHolder.getContext().getAuthentication());
  }

  public static PapUser requirePapUser(Authentication authentication) {
    if (authentication != null && authentication.getPrincipal() instanceof PapUser papUser) {
      return papUser;
    }
    throw new PaymentPlatformException(CommonErrorCode.FORBIDDEN, "You don't have permission to access this resource.");
  }

  public static Optional<PapUser> getPapUser() {
    return getPapUser(SecurityContextHolder.getContext().getAuthentication());
  }

  public static Optional<PapUser> getPapUser(Authentication authentication) {
    if (authentication != null && authentication.getPrincipal() instanceof PapUser papUser) {
      return Optional.of(papUser);
    }
    return Optional.empty();
  }

  public static Optional<String> getUsername() {
    return getUsername(SecurityContextHolder.getContext().getAuthentication());
  }

  public static Optional<String> getUsername(Authentication authentication) {
    if (authentication != null && authentication.getPrincipal() instanceof PapUser papUser) {
      return Optional.of(papUser.getUsername());
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
