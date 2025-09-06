package vn.com.mbbank.adminportal.core.security;

import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;
import vn.com.mbbank.adminportal.core.model.BitmaskValue;

import static org.springframework.http.HttpMethod.*;

@Component
@RequiredArgsConstructor
public class PermissionAuthorizationManager {
  private static final AuthorizationDecision GRANTED = new AuthorizationDecision(true);
  private static final AuthorizationDecision DENIED = new AuthorizationDecision(false);
  private final BitmaskPermissionEvaluator permissionEvaluator;

  private <T> AuthorizationManager<T> hasPermission(String module, BitmaskValue value) {
    return (authentication, context) -> {
      if (permissionEvaluator.hasPermission(authentication.get(), module, value)) {
        return GRANTED;
      }
      return DENIED;
    };
  }

  private <T> AuthorizationManager<T> hasPermission(String module1, String module2, BitmaskValue value) {
    return (authentication, context) -> {
      if (permissionEvaluator.hasPermission(authentication.get(), module1, value) || permissionEvaluator.hasPermission(authentication.get(), module2, value)) {
        return GRANTED;
      }
      return DENIED;
    };
  }

  public void configAdminPortalRequestMatcher(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(authorize -> authorize
        .dispatcherTypeMatchers(DispatcherType.ASYNC).permitAll()
        .requestMatchers(GET, "/actuator/**", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
            .requestMatchers(POST, "/login/**").permitAll()
            .requestMatchers(GET, "/login/**").access(hasPermission("user", BitmaskValue.INSERT))
            .requestMatchers(GET, "/roles/**").access(hasPermission("role", BitmaskValue.VIEW))
        .requestMatchers(GET, "/roles/**").access(hasPermission("role", BitmaskValue.VIEW))
        .requestMatchers(POST, "/roles**").access(hasPermission("role", BitmaskValue.INSERT))
        .requestMatchers(PUT, "/roles**").access(hasPermission("role", BitmaskValue.UPDATE))
        .requestMatchers(GET, "/permissions**").access(hasPermission("permission", BitmaskValue.VIEW))
                    .anyRequest().authenticated()
//            .requestMatchers(GET, "/user/permissions**").access(hasPermission("permission", BitmaskValue.VIEW))
    );
  }
}
