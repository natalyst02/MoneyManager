package vn.com.mbbank.adminportal.core.util;

import lombok.extern.log4j.Log4j2;
import vn.com.mbbank.adminportal.common.exception.BusinessErrorCode;
import vn.com.mbbank.adminportal.common.spi.ErrorCodeFactory;
import vn.com.mbbank.adminportal.common.util.Constant;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Log4j2
public class ErrorCodeFactoryImpl implements ErrorCodeFactory {
  private static final Map<String, BusinessErrorCode> errorCodeMap;

  static {
    var codes = new HashMap<String, BusinessErrorCode>();
    var duplications = Arrays.stream(ErrorCode.class.getFields())
        .filter(f -> Modifier.isStatic(f.getModifiers()) && f.getType().equals(BusinessErrorCode.class))
        .map(f -> {
          try {
            return (BusinessErrorCode) f.get(null);
          } catch (IllegalAccessException e) {
            log.error("Can't load error code into map", e);
            throw new RuntimeException(e);
          }
        })
        .filter(c -> codes.put(Constant.PREFIX_RESPONSE_CODE + c.code(), c) != null)
        .toList();
    if (!duplications.isEmpty()) {
      throw new RuntimeException("Found error code duplication: " + duplications);
    }
    errorCodeMap = Map.copyOf(codes);
  }

  @Override
  public BusinessErrorCode lookup(String code) {
    return errorCodeMap.get(code);
  }

  @Override
  public BusinessErrorCode lookup(String code, BusinessErrorCode defaultErrorCode) {
    return errorCodeMap.getOrDefault(code, defaultErrorCode);
  }
}
