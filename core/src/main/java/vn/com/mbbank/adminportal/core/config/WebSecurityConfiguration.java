package vn.com.mbbank.adminportal.core.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import vn.com.mbbank.adminportal.common.config.RedisClusterAdapter;
import vn.com.mbbank.adminportal.common.controller.ExceptionController;
import vn.com.mbbank.adminportal.common.security.JwtAuthenticationFilter;
import vn.com.mbbank.adminportal.common.thirdparty.keycloak.KeycloakClient;
import vn.com.mbbank.adminportal.common.thirdparty.keycloak.impl.KeycloakClientImpl;
import vn.com.mbbank.adminportal.common.util.JwtUtil;
import vn.com.mbbank.adminportal.common.util.RequestLoggingFilter;
import vn.com.mbbank.adminportal.common.util.RestClient;
import vn.com.mbbank.adminportal.common.util.Tracing;
import vn.com.mbbank.adminportal.core.security.KeycloakJwtAuthenticator;
import vn.com.mbbank.adminportal.core.security.PermissionAuthorizationManager;
import vn.com.mbbank.adminportal.core.service.internal.RoleServiceInternal;
import vn.com.mbbank.adminportal.core.service.internal.UserServiceInternal;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class WebSecurityConfiguration {
  private final ExceptionController exceptionController;
  private final UserServiceInternal userService;
  private final RoleServiceInternal roleService;
  private final RedisClusterAdapter redisClusterAdapter;
  private final PermissionAuthorizationManager permissionManager;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter authenticationFilter, RequestLoggingFilter requestLoggingFilter) throws Exception {
    permissionManager.configAdminPortalRequestMatcher(http);
    http.csrf(AbstractHttpConfigurer::disable)
        .cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()))
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(exception -> exception
            .accessDeniedHandler((request, response, accessDeniedException) -> {
              var init = Tracing.initIfNeed(request);
              try {
                exceptionController.handleAccessDeniedException(accessDeniedException, request, response);
              } finally {
                if (init) {
                  Tracing.clear();
                }
              }
            })
            .authenticationEntryPoint((request, response, authException) -> {
              var init = Tracing.initIfNeed(request);
              try {
                exceptionController.handleAuthenticationException(authException, request, response);
              } finally {
                if (init) {
                  Tracing.clear();
                }
              }
            }))
        .addFilterBefore(requestLoggingFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(authenticationFilter, RequestLoggingFilter.class);
    return http.build();
  }

  @Bean
  JwtAuthenticationFilter authenticationFilter(JwtUtil jwtUtil, RedisClusterAdapter redisClusterAdapter, UserServiceInternal userService, RoleServiceInternal roleService) {
    return new JwtAuthenticationFilter(new KeycloakJwtAuthenticator(jwtUtil,redisClusterAdapter, userService, roleService));
  }

  @Bean
  KeycloakClient keycloakClient(RestClient restClient, @Value("${authentication.keycloak.jwks-url}") String jwksUrl) {
    return new KeycloakClientImpl(restClient, jwksUrl);
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    var configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of("*"));
    configuration.setAllowedMethods(List.of("*"));
    configuration.setAllowedHeaders(List.of("*"));
    var source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}