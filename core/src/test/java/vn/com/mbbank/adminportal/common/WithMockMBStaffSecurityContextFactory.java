package vn.com.mbbank.adminportal.common;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import vn.com.mbbank.adminportal.common.model.MBStaff;

import java.util.List;

public class WithMockMBStaffSecurityContextFactory implements WithSecurityContextFactory<WithMockMBStaff> {
  @Override
  public SecurityContext createSecurityContext(WithMockMBStaff annotation) {
    var principal = new MBStaff().setUsername(annotation.username()).setTokenExpiredAt(System.currentTimeMillis() + 1800);
    var authentication = new UsernamePasswordAuthenticationToken(principal, "password", List.of());
    var context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(authentication);
    return context;
  }
}