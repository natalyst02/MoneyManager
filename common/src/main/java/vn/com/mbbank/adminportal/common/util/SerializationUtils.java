package vn.com.mbbank.adminportal.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import vn.com.mbbank.adminportal.common.exception.PaymentPlatformException;

@Log4j2
public final class SerializationUtils {
  public static  <T> String serializeAsString(ObjectMapper objectMapper, T object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new PaymentPlatformException(CommonErrorCode.INTERNAL_SERVER_ERROR, "can't serialize object", e);
    }
  }

  private SerializationUtils() {
    throw new UnsupportedOperationException();
  }
}
