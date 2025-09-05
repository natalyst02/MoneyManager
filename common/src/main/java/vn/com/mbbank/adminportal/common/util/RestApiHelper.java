package vn.com.mbbank.adminportal.common.util;

import org.apache.logging.log4j.ThreadContext;
import vn.com.mbbank.adminportal.common.exception.BusinessErrorCode;
import vn.com.mbbank.adminportal.common.exception.NSTCompletionException;
import vn.com.mbbank.adminportal.common.exception.PaymentPlatformException;
import vn.com.mbbank.adminportal.common.oauth.AccessToken;
import vn.com.mbbank.adminportal.common.oauth.OAuthClient;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

public final class RestApiHelper {
  private RestApiHelper() {
    throw new UnsupportedOperationException();
  }

  public static CompletableFuture<String> getAccessToken(String jwtToken, OAuthClient oAuthClient) {
    return jwtToken != null ? CompletableFuture.completedFuture(jwtToken) : oAuthClient.getAccessToken().thenApply(AccessToken::getToken);
  }

  public static String getOrCreateClientMessageId() {
    var clientMessageId = ThreadContext.get(Constant.CLIENT_MESSAGE_ID);
    if (clientMessageId == null) {
      clientMessageId = UUID.randomUUID().toString();
      ThreadContext.put(Constant.CLIENT_MESSAGE_ID, clientMessageId);
    }
    return clientMessageId;
  }

  public static void handleException(Throwable throwable, BusinessErrorCode timeoutErrorCode, Supplier<String> timeoutMsgSupplier, BusinessErrorCode errorErrorCode, Supplier<String> errorMsgSupplier) {
    throwable = CompletableFutures.unwrapException(throwable);
    if (throwable instanceof TimeoutException) {
      throw new NSTCompletionException(new PaymentPlatformException(timeoutErrorCode, timeoutMsgSupplier.get(), throwable));
    }
    throw new NSTCompletionException(new PaymentPlatformException(errorErrorCode, errorMsgSupplier.get(), throwable));
  }
}
