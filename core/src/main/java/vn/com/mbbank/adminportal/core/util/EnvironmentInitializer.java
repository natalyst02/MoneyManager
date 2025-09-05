package vn.com.mbbank.adminportal.core.util;

import vn.com.mbbank.adminportal.common.spi.ErrorCodeFactory;
import vn.com.mbbank.adminportal.common.util.Json;

import java.lang.invoke.MethodHandles;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class EnvironmentInitializer {

  public static void initialize() {
    System.setProperty("jasypt.encryptor.password", "phm@aczz!$^6");
    System.setProperty("service.response.prefix-code", "PAP" + "-");
    System.setProperty("service.code", "PP_ADMIN_PORTAL");
    System.setProperty("localIpValue", getLocalIp());
    try {
      var lookup = MethodHandles.lookup();
      lookup.ensureInitialized(ErrorCodeFactory.class);
      lookup.ensureInitialized(Json.class);
    } catch (IllegalAccessException e) {
      throw new IllegalArgumentException("Can't init java class", e);
    }
  }

  public static String getLocalIp() {
    try {
      return InetAddress.getLocalHost().getHostAddress();
    } catch (UnknownHostException e) {
      throw new IllegalArgumentException("Unable to determine local IP address.", e);
    }
  }

  private EnvironmentInitializer() {
    throw new UnsupportedOperationException();
  }
}
