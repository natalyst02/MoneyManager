package vn.com.mbbank.adminportal.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Log4j2
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private static final String BEAR_PREFIX = "Bearer ";
  private static final int TOKEN_START_INDEX = BEAR_PREFIX.length();
  private final JwtAuthenticator jwtAuthenticator;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    var authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (authorizationHeader == null || !authorizationHeader.startsWith(BEAR_PREFIX)) {
      filterChain.doFilter(request, response);
      return;
    }
    var token = authorizationHeader.substring(TOKEN_START_INDEX);
    var authentication = jwtAuthenticator.authenticate(token);
    if (authentication != null) {
      SecurityContextHolder.getContext().setAuthentication(authentication);
    } else {
      log.warn("Invalid Jwt Token: '{}'", token);
    }
    filterChain.doFilter(request, response);
  }
}
