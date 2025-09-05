package vn.com.mbbank.adminportal.core.util;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import vn.com.mbbank.adminportal.common.oauth.AccessToken;

import java.util.HashMap;
import java.util.Map;

import static vn.com.mbbank.adminportal.common.util.Constant.CLIENT_MESSAGE_ID;
import static vn.com.mbbank.adminportal.common.util.Constant.X_AUTH_USER;

@Log4j2
public class RequestHelper {

  private static Map<String, String> createJsonHeader(String clientMessageId) {
    Map<String, String> map = new HashMap<>();
    map.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    map.put(CLIENT_MESSAGE_ID, clientMessageId);

    return map;
  }

  public static Map<String, String> createJsonHeader(String clientMessageId, AccessToken accessToken) {
    var header = createJsonHeader(clientMessageId);

    if (accessToken != null && StringUtils.hasText(accessToken.getToken())) {
      header.put(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken.getToken());
    }

    return header;
  }

  public static Map<String, String> createJsonHeader(String clientMessageId, AccessToken accessToken, String username) {
    var header = createJsonHeader(clientMessageId, accessToken);
    header.put(X_AUTH_USER, username);
    return header;
  }

}
