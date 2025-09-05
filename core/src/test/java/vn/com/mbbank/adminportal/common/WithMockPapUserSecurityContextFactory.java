package vn.com.mbbank.adminportal.common;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.runtime.Generics;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.util.StringUtils;
import vn.com.mbbank.adminportal.common.model.PapUser;
import vn.com.mbbank.adminportal.common.util.Json;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class WithMockPapUserSecurityContextFactory implements WithSecurityContextFactory<WithMockPapUser> {
  private static final JsonReader.ReadObject<Map<String, Integer>> ROLE_PERMISSION_READ_OBJECT = Json.findReader(Generics.makeParameterizedType(Map.class, String.class, Integer.class));
  @Override
  public SecurityContext createSecurityContext(WithMockPapUser annotation) {
    var principal = new PapUser().setUsername(annotation.username()).setTokenExpiredAt(System.currentTimeMillis() + 1800)
            .setUserId(1L);
    if (StringUtils.hasText(annotation.permissions())) {
      principal.setPermissions(Json.decode(annotation.permissions().getBytes(StandardCharsets.UTF_8), ROLE_PERMISSION_READ_OBJECT));
    }
    var authentication = new UsernamePasswordAuthenticationToken(principal, "password", List.of());
    var context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(authentication);
    return context;
  }
}
