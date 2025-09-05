package vn.com.mbbank.adminportal.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.AsyncEvent;
import jakarta.servlet.AsyncListener;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;
import vn.com.mbbank.adminportal.common.model.HttpMessage;
import vn.com.mbbank.adminportal.common.model.PapUser;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.function.Predicate;

@RequiredArgsConstructor
public class RequestLoggingFilter extends OncePerRequestFilter {
  private static final int DEFAULT_MAX_PAYLOAD_LENGTH = 50;
  private static final String ACTUATOR_PREFIX = "/actuator/";
  private final boolean immediate;
  private final ObjectMapper objectMapper;
  private boolean includeQueryString;
  private boolean includeClientInfo;
  private boolean includeHeaders;
  private boolean includePayload;
  private boolean includeResponse;
  @Nullable
  private Predicate<String> headerPredicate;
  private int maxPayloadLength = DEFAULT_MAX_PAYLOAD_LENGTH;

  /**
   * Set whether the query string should be included in the log message.
   * <p>Should be configured using an {@code <init-param>} for parameter name
   * "includeQueryString" in the filter definition in {@code web.xml}.
   */
  public RequestLoggingFilter setIncludeQueryString(boolean includeQueryString) {
    this.includeQueryString = includeQueryString;
    return this;
  }

  /**
   * Return whether the query string should be included in the log message.
   */
  protected boolean isIncludeQueryString() {
    return this.includeQueryString;
  }

  /**
   * Set whether the client address and session id should be included in the
   * log message.
   * <p>Should be configured using an {@code <init-param>} for parameter name
   * "includeClientInfo" in the filter definition in {@code web.xml}.
   */
  public RequestLoggingFilter setIncludeClientInfo(boolean includeClientInfo) {
    this.includeClientInfo = includeClientInfo;
    return this;
  }

  /**
   * Return whether the client address and session id should be included in the
   * log message.
   */
  protected boolean isIncludeClientInfo() {
    return this.includeClientInfo;
  }

  /**
   * Set whether the request headers should be included in the log message.
   * <p>Should be configured using an {@code <init-param>} for parameter name
   * "includeHeaders" in the filter definition in {@code web.xml}.
   *
   * @since 4.3
   */
  public RequestLoggingFilter setIncludeHeaders(boolean includeHeaders) {
    this.includeHeaders = includeHeaders;
    return this;
  }

  /**
   * Return whether the request headers should be included in the log message.
   *
   * @since 4.3
   */
  protected boolean isIncludeHeaders() {
    return this.includeHeaders;
  }

  /**
   * Set whether the request payload (body) should be included in the log message.
   * <p>Should be configured using an {@code <init-param>} for parameter name
   * "includePayload" in the filter definition in {@code web.xml}.
   *
   * @since 3.0
   */
  public RequestLoggingFilter setIncludePayload(boolean includePayload) {
    this.includePayload = includePayload;
    return this;
  }

  /**
   * Return whether the request payload (body) should be included in the log message.
   *
   * @since 3.0
   */
  protected boolean isIncludePayload() {
    return this.includePayload;
  }

  public RequestLoggingFilter setIncludeResponse(boolean includeResponse) {
    this.includeResponse = includeResponse;
    return this;
  }

  public boolean isIncludeResponse() {
    return includeResponse;
  }

  /**
   * Configure a predicate for selecting which headers should be logged if
   * {@link #setIncludeHeaders(boolean)} is set to {@code true}.
   * <p>By default this is not set in which case all headers are logged.
   *
   * @param headerPredicate the predicate to use
   * @since 5.2
   */
  public RequestLoggingFilter setHeaderPredicate(@Nullable Predicate<String> headerPredicate) {
    this.headerPredicate = headerPredicate;
    return this;
  }

  /**
   * The configured {@link #setHeaderPredicate(Predicate) headerPredicate}.
   *
   * @since 5.2
   */
  @Nullable
  protected Predicate<String> getHeaderPredicate() {
    return this.headerPredicate;
  }

  /**
   * Set the maximum length of the payload body to be included in the log message.
   * Default is 50 characters.
   *
   * @since 3.0
   */
  public RequestLoggingFilter setMaxPayloadLength(int maxPayloadLength) {
    Assert.isTrue(maxPayloadLength >= 0, "'maxPayloadLength' must be greater than or equal to 0");
    this.maxPayloadLength = maxPayloadLength;
    return this;
  }

  /**
   * Return the maximum length of the payload body to be included in the log message.
   *
   * @since 3.0
   */
  protected int getMaxPayloadLength() {
    return this.maxPayloadLength;
  }

  /**
   * The default value is "false" so that the filter may log a "before" message
   * at the start of request processing and an "after" message at the end from
   * when the last asynchronously dispatched thread is exiting.
   */
  @Override
  protected boolean shouldNotFilterAsyncDispatch() {
    return false;
  }

  /**
   * Forwards the request to the next filter in the chain and delegates down to the subclasses
   * to perform the actual request logging both before and after the request is processed.
   *
   * @see #beforeRequest
   * @see #afterRequest
   */
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    boolean isFirstRequest = !isAsyncDispatch(request);

    HttpServletRequest requestToUse = request;
    if (isIncludePayload() && isFirstRequest && !(request instanceof ContentCachingRequestWrapper)) {
      requestToUse = new ContentCachingRequestWrapper(request, getMaxPayloadLength());
    }

    Tracing.init(requestToUse);

    boolean shouldLog = shouldLog(requestToUse);
    if (immediate && isFirstRequest && shouldLog) {
      beforeRequest(requestToUse, createMessage(requestToUse, null));
    }

    if (isFirstRequest
        && shouldLog
        && (request.getContentLength() == 0 || "GET".equals(request.getMethod()) || "DELETE".equals(request.getMethod()))) {
      request.setAttribute(LoggingHelper.START_TIME_KEY, System.nanoTime());

      ThreadContext.put(LoggingHelper.LOG_TYPE, "httprequest");
      ThreadContext.remove("duration");

      var authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication != null && authentication.getPrincipal() instanceof PapUser papUser) {
        ThreadContext.put(LoggingHelper.AUTHENTICATION_USER, papUser.getUsername());
        ThreadContext.put(LoggingHelper.SOURCE_APP_ID_KEY, papUser.getSourceAppId());
        request.setAttribute(LoggingHelper.USER_NAME_KEY, papUser.getUsername());
        request.setAttribute(LoggingHelper.SOURCE_APP_ID_KEY, papUser.getSourceAppId());

        var s = objectMapper.writeValueAsString(new HttpMessage<Void>(request));
        logger.info(s);
        ThreadContext.remove(LoggingHelper.LOG_TYPE);

        ThreadContext.remove(LoggingHelper.AUTHENTICATION_USER);
        ThreadContext.remove(LoggingHelper.SOURCE_APP_ID_KEY);
      } else {
        var s = objectMapper.writeValueAsString(new HttpMessage<Void>(request));
        logger.info(s);
        ThreadContext.remove(LoggingHelper.LOG_TYPE);
      }
    }

    HttpServletResponse responseToUse = response;
    if (isIncludeResponse() && !immediate && !(response instanceof ContentCachingResponseWrapper)) {
      responseToUse = new ContentCachingResponseWrapper(response);
    }

    try {
      filterChain.doFilter(requestToUse, responseToUse);
    } finally {
      try {
        if (!immediate && shouldLog && !isAsyncStarted(requestToUse)) {
          afterRequest(requestToUse, createMessage(requestToUse, responseToUse));
        }
      } finally {
        Tracing.clear();
        if (responseToUse instanceof ContentCachingResponseWrapper cachedResp) {
          if (requestToUse.isAsyncStarted()) {
            requestToUse.getAsyncContext().addListener(new AsyncListener() {
              public void onComplete(AsyncEvent asyncEvent) throws IOException {
                cachedResp.copyBodyToResponse();
              }

              public void onTimeout(AsyncEvent asyncEvent) throws IOException {
              }

              public void onError(AsyncEvent asyncEvent) throws IOException {
              }

              public void onStartAsync(AsyncEvent asyncEvent) throws IOException {
              }
            });
          } else {
            cachedResp.copyBodyToResponse();
          }
        }
      }
    }
  }

  /**
   * Create a log message for the given request.
   * <p>If {@code includeQueryString} is {@code true}, then the inner part
   * of the log message will take the form {@code request_uri?query_string};
   * otherwise the message will simply be of the form {@code request_uri}.
   */
  protected String createMessage(HttpServletRequest request, HttpServletResponse response) {
    StringBuilder msg = new StringBuilder();
    msg.append(request.getMethod()).append(' ');
    msg.append(request.getRequestURI());

    if (isIncludeQueryString()) {
      String queryString = request.getQueryString();
      if (queryString != null) {
        msg.append('?').append(queryString);
      }
    }

    if (isIncludeClientInfo()) {
      String client = request.getRemoteAddr();
      if (StringUtils.hasLength(client)) {
        msg.append(", client=").append(client);
      }
      HttpSession session = request.getSession(false);
      if (session != null) {
        msg.append(", session=").append(session.getId());
      }
      String user = request.getRemoteUser();
      if (user != null) {
        msg.append(", user=").append(user);
      }
    }

    if (isIncludeHeaders()) {
      HttpHeaders headers = new ServletServerHttpRequest(request).getHeaders();
      var predicate = headerPredicate;
      if (predicate != null) {
        Enumeration<String> names = request.getHeaderNames();
        while (names.hasMoreElements()) {
          String header = names.nextElement();
          if (!predicate.test(header)) {
            headers.set(header, "masked");
          }
        }
      }
      msg.append(", headers=").append(headers);
    }

    if (isIncludePayload()) {
      String payload = getMessagePayload(request);
      if (payload != null) {
        msg.append(", payload=").append(payload);
      }
    }
    if (response instanceof ContentCachingResponseWrapper cachedResponse) {
      msg.append(", response=").append(new String(cachedResponse.getContentAsByteArray(), StandardCharsets.UTF_8));
    }
    return msg.toString();
  }

  /**
   * Extracts the message payload portion of the message created by
   * {@link #createMessage(HttpServletRequest, HttpServletResponse)} when
   * {@link #isIncludePayload()} returns true.
   *
   * @since 5.0.3
   */
  @Nullable
  protected String getMessagePayload(HttpServletRequest request) {
    ContentCachingRequestWrapper wrapper =
        WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
    if (wrapper != null) {
      byte[] buf = wrapper.getContentAsByteArray();
      if (buf.length > 0) {
        int length = Math.min(buf.length, getMaxPayloadLength());
        try {
          return new String(buf, 0, length, wrapper.getCharacterEncoding());
        } catch (UnsupportedEncodingException ex) {
          return "[unknown]";
        }
      }
    }
    return null;
  }

  /**
   * Determine whether to call the {@link #beforeRequest}/{@link #afterRequest}
   * methods for the current request, i.e. whether logging is currently active
   * (and the log message is worth building).
   * <p>The default implementation always returns {@code true}. Subclasses may
   * override this with a log level check.
   *
   * @param request current HTTP request
   * @return {@code true} if the before/after method should get called;
   * {@code false} otherwise
   * @since 4.1.5
   */
  protected boolean shouldLog(HttpServletRequest request) {
    return logger.isDebugEnabled() && !request.getRequestURI().contains(ACTUATOR_PREFIX);
  }

  /**
   * Concrete subclasses should implement this method to write a log message
   * <i>before</i> the request is processed.
   *
   * @param request current HTTP request
   * @param message the message to log
   */
  protected void beforeRequest(HttpServletRequest request, String message) {
    logger.debug(message);
  }

  /**
   * Concrete subclasses should implement this method to write a log message
   * <i>after</i> the request is processed.
   *
   * @param request current HTTP request
   * @param message the message to log
   */
  protected void afterRequest(HttpServletRequest request, String message) {
    logger.debug(message);
  }
}
