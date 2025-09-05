package vn.com.mbbank.adminportal.common.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import vn.com.mbbank.adminportal.common.model.response.Response;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.function.Supplier;

public final class LoggingHelper {
  private LoggingHelper() {
    throw new UnsupportedOperationException();
  }


  public static final String START_TIME_KEY = "startTime";
  public static final String ACTUATOR_PREFIX = "/actuator/";
  public static final String USER_NAME_KEY = "username";
  public static final String AUTHENTICATION_USER = "authenticationUser";
  public static final String SOURCE_APP_ID_KEY = "sourceAppId";
  public static final String LOG_TYPE = "logType";

  public static void logResponse(HttpServletRequest request, HttpServletResponse servletResponse, Response<?> response, Runnable logFunction) {
    if (shouldSkipLog(request)) {
      return;
    }
    var startObj = request.getAttribute(START_TIME_KEY);
    if (startObj instanceof Long start) {
      long elapsed = System.nanoTime() - start;
      ThreadContext.put("duration", String.format("%.3f", elapsed / 1000000.0));
    }
    ThreadContext.put(LOG_TYPE, "httpresponse");
    var status = servletResponse.getStatus();
    ThreadContext.put("responseCode", "" + status);
    if (status != HttpStatus.OK.value()) {
      ThreadContext.put("soaErrorCode", response.getSoaErrorCode());
      ThreadContext.put("originalService", response.getOriginalService());
      ThreadContext.put("errorPath", response.getPath());
    }

    var usernameObj = request.getAttribute(USER_NAME_KEY);
    if (usernameObj instanceof String username) {
      ThreadContext.put(AUTHENTICATION_USER, username);
    }
    var sourceAppIdObj = request.getAttribute(SOURCE_APP_ID_KEY);
    if (sourceAppIdObj instanceof String sourceAppId) {
      ThreadContext.put(SOURCE_APP_ID_KEY, sourceAppId);
    }

    logFunction.run();

    ThreadContext.remove(LOG_TYPE);
    ThreadContext.remove("serviceMessageId");
    ThreadContext.remove("responseCode");
    if (status != HttpStatus.OK.value()) {
      ThreadContext.remove("soaErrorCode");
      ThreadContext.remove("originalService");
      ThreadContext.remove("errorPath");
    }

    if (usernameObj instanceof String) {
      ThreadContext.remove(AUTHENTICATION_USER);
    }
    if (sourceAppIdObj instanceof String) {
      ThreadContext.remove(SOURCE_APP_ID_KEY);
    }
  }

  public static boolean shouldSkipLog(HttpServletRequest request) {
    return request.getRequestURI().contains(ACTUATOR_PREFIX);
  }

  public static boolean shouldLog(ServerHttpRequest request) {
    return !request.getURI().getPath().contains(ACTUATOR_PREFIX);
  }

  public static <T> T getWithLogConsumeTime(Logger log, Supplier<String> messagSupplier, Supplier<T> function) {
    String message = messagSupplier.get();
    var startTime = OffsetDateTime.now();
    log.info("Start calling {}, time now is {}", message, startTime);
    var data = function.get();
    var endTime = OffsetDateTime.now();
    log.info("Finish calling {}, time now is {}, takes {} ms", message, endTime, Duration.between(startTime, endTime).toMillis());
    return data;
  }
}
