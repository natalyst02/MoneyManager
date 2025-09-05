package vn.com.mbbank.adminportal.common.spi;

import vn.com.mbbank.adminportal.common.exception.BusinessErrorCode;

import java.util.ServiceLoader;

public interface ErrorCodeFactory {
  ErrorCodeFactory INSTANCE = load();

  static ErrorCodeFactory load() {
    return load(null);
  }

  static ErrorCodeFactory load(ClassLoader classLoader) {
    var factories = classLoader != null ? ServiceLoader.load(ErrorCodeFactory.class, classLoader)
        : ServiceLoader.load(ErrorCodeFactory.class);
    var factoryIter = factories.iterator();
    if (factoryIter.hasNext()) {
      return factoryIter.next();
    }
    return (code, businessErrorCode) -> null;
  }

  default BusinessErrorCode lookup(String code) {
    return lookup(code, null);
  }

  BusinessErrorCode lookup(String code, BusinessErrorCode defaultErrorCode);
}
