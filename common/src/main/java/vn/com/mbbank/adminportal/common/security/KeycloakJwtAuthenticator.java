package vn.com.mbbank.adminportal.common.security;

import com.dslplatform.json.JsonReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import vn.com.mbbank.adminportal.common.jws.JWK;
import vn.com.mbbank.adminportal.common.jws.JWSInput;
import vn.com.mbbank.adminportal.common.jws.KeyType;
import vn.com.mbbank.adminportal.common.jws.RSASignatures;
import vn.com.mbbank.adminportal.common.model.MBStaff;
import vn.com.mbbank.adminportal.common.thirdparty.keycloak.KeycloakClient;
import vn.com.mbbank.adminportal.common.util.Json;

import java.security.PublicKey;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
public class KeycloakJwtAuthenticator implements JwtAuthenticator {
  private static final long PUBLIC_KEYS_TTL = 86_400_000;  //1d
  private static final long RELOAD_INTERVAL_MS = 120_000;  //2m
  private static final long ISSUED_INTERVAL_DELAY_SECS = 1200;  //20m
  private static final JsonReader.ReadObject<MBStaff> MB_STAFF_READ_OBJECT = Json.findReader(MBStaff.class);
  private final KeycloakClient keycloakClient;
  private Map<String, PublicKey> publicKeyCache;
  private volatile long lastUpdatedTime;

  @Override
  public Authentication authenticate(String token) {
    var input = new JWSInput(token);
    var kid = input.getHeader().getKeyId();
    var publicKey = getPublicKey(kid);
    if (RSASignatures.verify(input, publicKey)) {
      var staff = Json.decode(input.getContent(), MB_STAFF_READ_OBJECT);
      if (staff.getTokenExpiredAt() != null || staff.getTokenIssuedAt() != null) {
        var currentSeconds = System.currentTimeMillis() / 1000;
        if ((staff.getTokenExpiredAt() != null && staff.getTokenExpiredAt() <= currentSeconds)
            || (staff.getTokenIssuedAt() != null && staff.getTokenIssuedAt() > currentSeconds + ISSUED_INTERVAL_DELAY_SECS)) {
          return null;
        }
      }
      return new UsernamePasswordAuthenticationToken(staff, input, List.of());
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
}
