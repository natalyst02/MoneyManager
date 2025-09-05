package vn.com.mbbank.adminportal.core.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.util.matcher.RequestMatcher;
import java.util.Map;

public class MethodAndQueryParamRequestMatcher implements RequestMatcher {

  private final String urlPattern;
  private final String queryParamKey;
  private final String queryParamValue;
  private final String method;

  public MethodAndQueryParamRequestMatcher(String url, String key, String value, String method) {
    this.urlPattern = url;
    this.queryParamKey = key;
    this.queryParamValue = value;
    this.method = method;
  }

  @Override
  public boolean matches(HttpServletRequest request) {
    if (!request.getMethod().equalsIgnoreCase(method)) {
      return false;
    }
    var requestUri = request.getRequestURI();
    if (!requestUri.endsWith(urlPattern)) {
      return false;
    }
    Map<String, String[]> parameters = request.getParameterMap();
    var paramValues = parameters.get(queryParamKey);
    if (paramValues != null) {
      for (var value : paramValues) {
        if (value.equals(queryParamValue)) {
          return true;
        }
      }
    }
    return false;
  }
}

