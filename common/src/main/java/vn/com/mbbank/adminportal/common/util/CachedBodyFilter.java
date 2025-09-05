package vn.com.mbbank.adminportal.common.util;

import jakarta.servlet.Filter;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.core.config.Order;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import java.io.IOException;
import java.util.List;


@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CachedBodyFilter implements Filter {

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws ServletException, IOException {

    HttpServletRequest req = (HttpServletRequest) request;
    String fullUrl = req.getRequestURL().toString();
    List<String> forwardPaths = List.of("citad-transfer", "batch-transfer", "napas/ibft-reconcile");

    boolean isMultipart = req.getContentType() != null && req.getContentType().startsWith("multipart/form-data");

    boolean matchPath = forwardPaths.stream().anyMatch(fullUrl::contains);

    if (request instanceof HttpServletRequest && isMultipart && matchPath) {
      CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(req);
      chain.doFilter(cachedRequest, response);
    } else {
      chain.doFilter(request, response);
    }
  }
}
