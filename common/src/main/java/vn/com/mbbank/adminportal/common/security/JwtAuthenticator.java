package vn.com.mbbank.adminportal.common.security;

import org.springframework.security.core.Authentication;

public interface JwtAuthenticator {
  Authentication authenticate(String token);
}
