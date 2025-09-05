package vn.com.mbbank.adminportal.common;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockMBStaffSecurityContextFactory.class)
public @interface WithMockMBStaff {
  String username() default "mbstaff";
}
