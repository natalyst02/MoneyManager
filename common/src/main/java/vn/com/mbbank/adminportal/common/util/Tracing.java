package vn.com.mbbank.adminportal.common.util;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.ThreadContext;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Tracing {
  public static void init() {
    ThreadContext.put(Constant.CLIENT_MESSAGE_ID, UUID.randomUUID().toString());
  }

  public static void init(HttpServletRequest request) {
    var clientMessageId = request.getHeader(Constant.CLIENT_MESSAGE_ID);
    if (clientMessageId == null) {
      clientMessageId = (String) request.getAttribute(Constant.CLIENT_MESSAGE_ID);
    }
    if (clientMessageId == null) {
      clientMessageId = UUID.randomUUID().toString();
      request.setAttribute(Constant.CLIENT_MESSAGE_ID, clientMessageId);
    }
    var path = (String) request.getAttribute(Constant.PATH);
    if (path == null) {
      path = request.getRequestURI();
      request.setAttribute(Constant.PATH, path);
    }
    ThreadContext.putAll(Map.of(Constant.CLIENT_MESSAGE_ID, clientMessageId,
        Constant.PATH, path, "keeptime", "high"));
  }

  public static boolean initIfNeed(HttpServletRequest request) {
    if (ThreadContext.isEmpty()) {
      init(request);
      return true;
    }
    return false;
  }

  public static void clear() {
    ThreadContext.clearMap();
  }

  public static <T> CompletableFuture<T> instrument(CompletableFuture<T> future) {
    return instrument(future, ThreadContext.getImmutableContext());
  }

  public static <T> CompletableFuture<T> instrument(CompletableFuture<T> future, Map<String, String> context) {
    var result = new CompletableFuture<T>();
    result.whenComplete((v, e) -> ThreadContext.clearMap());
    future.whenComplete((v, e) -> {
      ThreadContext.clearMap();
      ThreadContext.putAll(context);
      if (e != null) {
        result.completeExceptionally(e);
      } else {
        result.complete(v);
      }
    });
    return result;
  }

  private Tracing() {
    throw new UnsupportedOperationException();
  }
}
