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
import vn.com.mbbank.adminportal.common.util.Json;
import vn.com.mbbank.adminportal.core.service.internal.RoleServiceInternal;
import vn.com.mbbank.adminportal.core.service.internal.UserServiceInternal;
import vn.com.mbbank.adminportal.core.thirdparty.keycloak.KeycloakInternalClient;
import vn.com.mbbank.adminportal.core.util.Constant;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

import static vn.com.mbbank.adminportal.core.util.Constant.CACHE_OTP;

@Log4j2
@RequiredArgsConstructor
public class PapUserAuthenticator implements JwtAuthenticator {
  private static final long PUBLIC_KEYS_TTL = 86_400_000;  //1d
  private static final long RELOAD_INTERVAL_MS = 120_000;  //2m
  private static final long ISSUED_INTERVAL_DELAY_SECS = 1200;  //20m
  private static final JsonReader.ReadObject<MBStaff> MB_STAFF_READ_OBJECT = Json.findReader(MBStaff.class);
  private final KeycloakClient keycloakClient;
  private final KeycloakInternalClient keycloakInternalClient;
  private Map<String, PublicKey> publicKeyCache;
  private volatile long lastUpdatedTime;
  private final UserServiceInternal userService;
  private final RoleServiceInternal roleService;
  private final RedisClusterAdapter redisClusterAdapter;

  @Override
  public Authentication authenticate(String token) {
    var input = new JWSInput(token);
    var kid = input.getHeader().getKeyId();
    var publicKey = getPublicKey(kid);
    if (publicKey == null) {
      log.warn("Can't find public key for kid: {}", kid);
      return null;
    }
    if (RSASignatures.verify(input, publicKey)) {
      try {
        var mbStaff = Json.decode(input.getContent(), MB_STAFF_READ_OBJECT);
        if (mbStaff.getTokenExpiredAt() != null || mbStaff.getTokenIssuedAt() != null) {
          var currentSeconds = System.currentTimeMillis() / 1000;
          if ((mbStaff.getTokenExpiredAt() != null && mbStaff.getTokenExpiredAt() <= currentSeconds)
              || (mbStaff.getTokenIssuedAt() != null && mbStaff.getTokenIssuedAt() > currentSeconds + ISSUED_INTERVAL_DELAY_SECS)) {
            return null;
          }
        }
        var username = mbStaff.getUsername();
        var isValidToken = redisClusterAdapter.computeIfAbsent(Constant.PAP_VALID_TOKEN + username, Boolean.class, Constant.PAP_VALID_TOKEN_DURATION,
            () -> keycloakInternalClient.validateToken(token).join());
        if (isValidToken == null || !isValidToken) {
          redisClusterAdapter.delete(Constant.PAP_VALID_TOKEN + username);
          return new UsernamePasswordAuthenticationToken(mbStaff, input);
        }
        var sessionState = mbStaff.getSessionState();
        var otpCacheKey = CACHE_OTP + username + ":" + sessionState;
        var otpVerified = redisClusterAdapter.get(otpCacheKey, Boolean.class);
        if (otpVerified == null || !otpVerified) {
          return new UsernamePasswordAuthenticationToken(mbStaff, input);
        }
        var papUser = redisClusterAdapter.computeIfAbsent(Constant.PAP_USER_KEY + username, PapUser.class, 300,
            () -> initPapUser(mbStaff));
        return new UsernamePasswordAuthenticationToken(papUser, input, List.of());
      } catch (Exception e) {
        log.warn("Can't decode token jwt: '{}'", token, e);
      }
    }
    return null;
  }

  private PublicKey getPublicKey(String kid) {
    var currentTime = System.currentTimeMillis();
    if (currentTime > lastUpdatedTime + PUBLIC_KEYS_TTL) {
      reloadCache();
    }
    var keys = publicKeyCache;
    var key = (keys != null) ? keys.get(kid) : null;
    if (key == null && reloadCache()) {
      keys = publicKeyCache;
      key = (keys != null) ? keys.get(kid) : null;
    }
    return key;
  }

  private synchronized boolean reloadCache() {
    try {
      var currentTime = System.currentTimeMillis();
      if (currentTime > lastUpdatedTime + RELOAD_INTERVAL_MS) {
        var jwkSet = keycloakClient.getJWKSet().join();
        publicKeyCache = jwkSet.getKeys().stream()
            .filter(jwk -> jwk.getPublicKeyUse() == JWK.Use.SIG && KeyType.RSA.equals(jwk.getKeyType()))
            .collect(Collectors.toMap(JWK::getKeyId, JWK::toPublicKey));
        lastUpdatedTime = currentTime;
        return true;
      }
    } catch (CompletionException e) {
      log.error("can't get jwk set from keyloak", e);
    }
    return false;
  }

  private PapUser initPapUser(MBStaff mbStaff) {
    var user = userService.getActiveUserByUsername(mbStaff.getUsername());
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
        .setSourceAppId(mbStaff.getSourceAppId())
        .setTokenExpiredAt(mbStaff.getTokenExpiredAt())
        .setPermissions(userPermission)
        .setRoleIds(roleIds);
  }
}
