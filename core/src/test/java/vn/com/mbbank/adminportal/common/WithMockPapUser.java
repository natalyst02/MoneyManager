package vn.com.mbbank.adminportal.common;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockPapUserSecurityContextFactory.class)
public @interface WithMockPapUser {
  String username() default "papUser";

  String permissions() default """
      {
        "role": 31,
        "user": 31,
        "permission": 1
      }
      """;
}
