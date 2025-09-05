package vn.com.mbbank.adminportal.core.util;

import vn.com.mbbank.adminportal.common.model.OpType;
import vn.com.mbbank.adminportal.common.model.SyncMessage;
import vn.com.mbbank.adminportal.common.util.RestApiHelper;

import java.time.OffsetDateTime;

public class MessageHelper {
  private MessageHelper() {
    throw new UnsupportedOperationException();
  }

  public static <T> SyncMessage<T> toSyncMessage(OpType opType, T message) {
    return new SyncMessage<T>()
        .setClientMessageId(RestApiHelper.getOrCreateClientMessageId())
        .setIat(OffsetDateTime.now())
        .setAction(opType)
        .setData(message);
  }

  public static <T> SyncMessage<T> toSyncMessage(OpType opType, T message, OffsetDateTime updatedAt, String target) {
    return new SyncMessage<T>()
        .setClientMessageId(RestApiHelper.getOrCreateClientMessageId())
        .setIat(updatedAt)
        .setAction(opType)
        .setData(message)
        .setTarget(target);
  }
}
