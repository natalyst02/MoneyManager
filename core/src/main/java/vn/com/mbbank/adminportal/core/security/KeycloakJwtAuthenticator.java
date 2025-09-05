package vn.com.mbbank.adminportal.core.security;

import com.dslplatform.json.JsonReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import vn.com.mbbank.adminportal.common.config.RedisClusterAdapter;
import vn.com.mbbank.adminportal.common.jws.JWK;
import vn.com.mbbank.adminportal.common.jws.JWSInput;
import vn.com.mbbank.adminportal.common.jws.KeyType;
import vn.com.mbbank.adminportal.common.jws.RSASignatures;
import vn.com.mbbank.adminportal.common.model.MBStaff;
import vn.com.mbbank.adminportal.common.model.PapUser;
import vn.com.mbbank.adminportal.common.security.JwtAuthenticator;
import vn.com.mbbank.adminportal.common.thirdparty.keycloak.KeycloakClient;
import vn.com.mbbank.adminportal.common.util.Constant;
import vn.com.mbbank.adminportal.common.util.Json;
import vn.com.mbbank.adminportal.common.util.JwtUtil;
import vn.com.mbbank.adminportal.core.service.internal.RoleServiceInternal;
import vn.com.mbbank.adminportal.core.service.internal.UserServiceInternal;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
public class KeycloakJwtAuthenticator implements JwtAuthenticator {

  private final JwtUtil jwtUtil;
  private final RedisClusterAdapter redisClusterAdapter;
  private final UserServiceInternal userService;
  private final RoleServiceInternal roleService;

  @Override
  public Authentication authenticate(String token) {
    token = token.split(" ")[1];
    if(jwtUtil.validateToken(token)) {
      String username = jwtUtil.extractUsername(token);
      PapUser papUser;
      try {
        papUser = redisClusterAdapter.get(Constant.PAP_USER_KEY + username, PapUser.class);
        if (papUser == null) {
          papUser= initPapUser(username);
        }
      }
      catch (Exception e) {
        papUser = initPapUser(username);
      }

      return new UsernamePasswordAuthenticationToken(papUser, token, List.of());
    }

    return null;
  }

  private PapUser initPapUser(String username) {
    var user = userService.getActiveUserByUsername(username);
    var userId = user.getId();
    var rolePermissions = roleService.getRolesPermissionsByActiveRole(userId);
    var roleIds = new ArrayList<Long>();
    var userPermission = new HashMap<String, Integer>();
    for (var rolePermission : rolePermissions) {
      roleIds.add(rolePermission.getRoleId());
      var permissions = rolePermission.getPermissions();
      permissions.keySet().forEach(key -> userPermission.merge(key, permissions.get(key), (oldBitmask, newBitmask) -> oldBitmask | newBitmask));
    }
    return new PapUser().setUserId(userId)
            .setUsername(user.getUsername())
            .setPermissions(userPermission)
            .setRoleIds(roleIds);
  }

}
