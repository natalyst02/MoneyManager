package vn.com.mbbank.adminportal.core.thirdparty.keycloak;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import vn.com.mbbank.adminportal.common.ApplicationTest;
import vn.com.mbbank.adminportal.common.exception.PaymentPlatformException;
import vn.com.mbbank.adminportal.core.thirdparty.keycloak.model.request.AssignRoleRequest;
import vn.com.mbbank.adminportal.core.util.Constant;
import vn.com.mbbank.adminportal.core.util.ErrorCode;

import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KeycloakInternalClientTest extends ApplicationTest {
  @Autowired
  private KeycloakInternalClient keycloakInternalClient;

  @Test
  void assignRole_success() {
    var request = new AssignRoleRequest()
        .setClientRole(true)
        .setComposite(false)
        .setName(Constant.DEFAULT_ROLE)
        .setId("4aaed715-f0f3-4898-b07d-a66061c94f07")
        .setContainerId("dc8fec67-0803-4057-a6d0-59af194098c7");
    assertTrue(keycloakInternalClient.assignRoleToUser(request, "9afed056-4e28-410b-9122-12e6f91d0c72").join());
  }

  @Test
  void assignRole_roleNotFound() {
    var request = new AssignRoleRequest()
        .setClientRole(true)
        .setComposite(false)
        .setName(Constant.DEFAULT_ROLE)
        .setId("4aaed715-f0f3-4898-b07d-a66061c94f071")
        .setContainerId("dc8fec67-0803-4057-a6d0-59af194098c7");
    var exception = assertThrows(CompletionException.class, () -> keycloakInternalClient.assignRoleToUser(request, "9afed056-4e28-410b-9122-12e6f91d0c72").join());
    assertTrue(exception.getCause() instanceof PaymentPlatformException be && be.getErrorCode().code().equals(ErrorCode.ASSIGN_ROLE_ERROR.code()) && be.getMessage().equals("Can not assign role to user due: Role not found"));
  }

  @Test
  void revokeRole_success() {
    var request = new AssignRoleRequest()
        .setClientRole(true)
        .setComposite(false)
        .setName(Constant.DEFAULT_ROLE)
        .setId("4aaed715-f0f3-4898-b07d-a66061c94f07")
        .setContainerId("dc8fec67-0803-4057-a6d0-59af194098c7");
    assertTrue(keycloakInternalClient.revokeRoleFromUser(request, "9afed056-4e28-410b-9122-12e6f91d0c72").join());
  }

  @Test
  void revokeRole_roleNotFound() {
    var request = new AssignRoleRequest()
        .setClientRole(true)
        .setComposite(false)
        .setName(Constant.DEFAULT_ROLE)
        .setId("4aaed715-f0f3-4898-b07d-a66061c94f071")
        .setContainerId("dc8fec67-0803-4057-a6d0-59af194098c7");
    var exception = assertThrows(CompletionException.class, () -> keycloakInternalClient.revokeRoleFromUser(request, "9afed056-4e28-410b-9122-12e6f91d0c72").join());
    assertInstanceOf(PaymentPlatformException.class, exception.getCause());
  }

  @Test
  void getUserInfo_success() {
    var response = keycloakInternalClient.getUserInfo("tunghv").join();
    assertFalse(response.isEmpty());
    assertEquals(response.get(0).getUsername(), "tunghv");
  }

  @Test
  void getUserInfo_notFound() {
    var exception = assertThrows(CompletionException.class, () -> keycloakInternalClient.getUserInfo("quyyyy").join());
    assertTrue(exception.getCause() instanceof PaymentPlatformException be && be.getErrorCode().code().equals(ErrorCode.GET_KEYCLOAK_USER_NOT_FOUND.code()) && be.getMessage().equals("Keycloak user quyyyy not found"));
  }

  @Test
  void validateToken_success() {
    assertFalse(keycloakInternalClient.validateToken("eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJGaWVQekJZY1BhVkl5Q19nZWs5Zzdqc1ZWV2JYZExROUVQTHUxMm02RGx3In0.eyJleHAiOjE3MzM5NzcxNTAsImlhdCI6MTczMzk3NTM1MCwiYXV0aF90aW1lIjoxNzMzOTc1MjkwLCJqdGkiOiI2ZTVhYjcyYS1hYWVlLTQ1NzYtOGZjNS0wNzVlYTY5NDgxYzEiLCJpc3MiOiJodHRwczovL2tleWNsb2FrLWludGVybmFsLXVhdC5tYmJhbmsuY29tLnZuL2F1dGgvcmVhbG1zL2ludGVybmFsIiwiYXVkIjpbImhjbS1zZXJ2aWNlX2JrIiwicG1oLXNlcnZpY2UtcHJlLXByb2QiLCJyZWFsbS1tYW5hZ2VtZW50IiwiaGNtLXNlcnZpY2UtdWF0LTEiLCJjcm0tZnJvbnRlbmQiLCJwbWgtc2VydmljZS1kZXYiLCJzYWxlLXNlcnZpY2UiLCJwYW1zLXNlcnZpY2UtdjIiLCJwbWgtc2VydmljZSIsInBhbXMtc2VydmljZS12MSIsImxlZ2FsLXByb3Zpc2lvbi1zZXJ2aWNlIiwibXltYi1zZXJ2aWNlLXYxIiwibXltYi1zZXJ2aWNlLXYyIiwiaGNtLXNlcnZpY2UiLCJwbWgtc2VydmljZS11YXQiLCJlY20tc21hcnQtc2Nhbi1tY3JzIiwiZWNtLWFpLWltYWdlLXJlcG9zaXRvcnktbWNycyIsIm1iZXJhcHAiLCJoY20tc2VydmljZS11YXQiLCJhY2NvdW50Il0sInN1YiI6IjlhZmVkMDU2LTRlMjgtNDEwYi05MTIyLTEyZTZmOTFkMGM3MiIsInR5cCI6IkJlYXJlciIsImF6cCI6InBvcnRhbC1mcm9udGVuZCIsIm5vbmNlIjoiMzUzOWQ2OTgtMDE3Yi00MDcwLThiMzktM2M4YTJkNzkzNzZmIiwic2Vzc2lvbl9zdGF0ZSI6ImE4NzEzNTdiLTRlNzctNDZjZi1hYjZiLWJkM2E0M2JkYzQ2MSIsImFjciI6IjAiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cDovL2xvY2FsaG9zdDo0MzAwIiwiaHR0cHM6Ly93ZWJwb3J0YWwtdWF0Lm1iYmFuay5jb20udm4iLCIqIiwiaHR0cDovL2xvY2FsaG9zdDo0MjAwIiwiaHR0cDovLzEwLjIxNS4yNTQuNzQ6ODA4MCJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJkZWZhdWx0LXJvbGVzLWNybSIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiaGNtLXNlcnZpY2VfYmsiOnsicm9sZXMiOlsiSENNLU1CRVIiLCJIQ00tQURNLVNVUFBFUi1VU0VSIiwiSENNLU1BTkFHRVIiLCJIQ00tQURNSU4iXX0sInBtaC1zZXJ2aWNlLXByZS1wcm9kIjp7InJvbGVzIjpbIlBNSF9LU1ZfUFRUIiwiUE1IX0NWX1BUVCIsIlBNSF9BRE1JTiJdfSwicmVhbG0tbWFuYWdlbWVudCI6eyJyb2xlcyI6WyJ2aWV3LXJlYWxtIiwiaW1wZXJzb25hdGlvbiIsImNyZWF0ZS1jbGllbnQiLCJtYW5hZ2UtdXNlcnMiLCJ2aWV3LWF1dGhvcml6YXRpb24iLCJxdWVyeS1jbGllbnRzIiwicXVlcnktdXNlcnMiLCJ2aWV3LWV2ZW50cyIsInZpZXctdXNlcnMiLCJ2aWV3LWNsaWVudHMiLCJtYW5hZ2UtYXV0aG9yaXphdGlvbiIsIm1hbmFnZS1jbGllbnRzIiwicXVlcnktZ3JvdXBzIl19LCJoY20tc2VydmljZS11YXQtMSI6eyJyb2xlcyI6WyJIQ00tTUJFUiIsIkhDTS1HT0EtU1VQRVJBRE1JTiIsIkhDTS1BQlMtUUxUVCIsIkhDTS1BRE0tT1BOIiwiSENNLVBOU19QSEVfRFVZRVRfSE9QX0RPTkciLCJIQ00tTVAtQ1ZOUyIsIkhDTS1DQi1TVVBFUiIsIkhDTS1BRE0tU1VQUEVSLVVTRVIiLCJIQ00tTE5ELUFETSIsIkhDTS1DQi1DVk5TIiwiSENNLU1BTkFHRVIiLCJIQ00tTVAtQ0JRTCIsIkhDTS1DQi1DQlFMIiwiSENNLUFETUlOIl19LCJjcm0tZnJvbnRlbmQiOnsicm9sZXMiOlsidW1hX3Byb3RlY3Rpb24iLCJtYW5hZ2UtYXV0aG9yaXphdGlvbiIsInZpZXctYXV0aG9yaXphdGlvbiJdfSwicG1oLXNlcnZpY2UtZGV2Ijp7InJvbGVzIjpbIlBNSF9BRE1JTiIsIlBNSF9NQkVSIl19LCJzYWxlLXNlcnZpY2UiOnsicm9sZXMiOlsidW1hX3Byb3RlY3Rpb24iXX0sInBhbXMtc2VydmljZS12MiI6eyJyb2xlcyI6WyJQQU1TLU1CRVIiXX0sInBtaC1zZXJ2aWNlIjp7InJvbGVzIjpbIlBNSF9BRE1JTiIsIlBNSF9NQkVSIl19LCJwYW1zLXNlcnZpY2UtdjEiOnsicm9sZXMiOlsiUEFNUy1NQkVSIl19LCJsZWdhbC1wcm92aXNpb24tc2VydmljZSI6eyJyb2xlcyI6WyJDSFVZRU5fVklFTiJdfSwibXltYi1zZXJ2aWNlLXYxIjp7InJvbGVzIjpbIk1ZLU1CLU1CRVIiXX0sIm15bWItc2VydmljZS12MiI6eyJyb2xlcyI6WyJNWS1NQi1NQkVSIl19LCJoY20tc2VydmljZSI6eyJyb2xlcyI6WyJIQ00tTUJFUiIsIkhDTS1HT0EtU1VQRVJBRE1JTiIsIkhDTS1BQlMtUUxUVCIsIkhDTS1BRE0tT1BOIiwiSENNLVBOU19QSEVfRFVZRVRfSE9QX0RPTkciLCJIQ00tQURNLVNVUFBFUi1VU0VSIiwiSENNLU1QLUNWTlMiLCJIQ00tQ0ItU1VQRVIiLCJIQ00tTE5ELUFETSIsIkhDTS1DQi1DVk5TIiwiSENNLU1QLUNCUUwiLCJIQ00tTUFOQUdFUiIsIkhDTS1DQi1DQlFMIiwiSENNLUFETUlOIl19LCJwbWgtc2VydmljZS11YXQiOnsicm9sZXMiOlsiUE1IX1JPTEVfREVNTyIsIlBNSF9NQkVSIl19LCJlY20tc21hcnQtc2Nhbi1tY3JzIjp7InJvbGVzIjpbImNhcHR1cmVfdXNlciJdfSwiZWNtLWFpLWltYWdlLXJlcG9zaXRvcnktbWNycyI6eyJyb2xlcyI6WyJlY20tYWktaW1hZ2UtcmVwb3NpdG9yeS1hZG1pbiIsImVjbS1haS1pbWFnZS1yZXBvc2l0b3J5LXVzZXIiXX0sIm1iZXJhcHAiOnsicm9sZXMiOlsidW1hX3Byb3RlY3Rpb24iXX0sImhjbS1zZXJ2aWNlLXVhdCI6eyJyb2xlcyI6WyJIQ00tMDA5OCIsIkhDTS1NQkVSIiwiSENNLU1TUzEiLCJIQ00tR09BLU1TUyIsIkhDTS1NU1MiLCJIQ00tQTEiXX0sImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoib3BlbmlkIHByb2ZpbGUgZW1haWwiLCJzaWQiOiJhODcxMzU3Yi00ZTc3LTQ2Y2YtYWI2Yi1iZDNhNDNiZGM0NjEiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsInByZWZlcnJlZF91c2VybmFtZSI6InF1eWR2MiIsImVtYWlsIjoicXV5ZHYyQG1iYmFuay5jb20udm4ifQ.ijwuyHYgHxg6Qp9nsGSpetDpIXtxo_kBwxI5CCo8cAlOAbKF4vZAGkaiboWQgrJdRtOvJ9VhQrg6p9swzkbznG4EbhtzGW5GNCPXRlXKaZK8N8-Hko5tu1BA26wKbrqqtftUxs0-yUBLFU5ygrWktXXQXjqUbtcI1y37V4g3k1Sb_kJAElcYVkpVSmWAfU1q5som5X9jDRFA2yVbaDySbeaBmyD7LXBpOxFR8NGJoCUvrvCG7RSeQkZy2SZ_EQlWMfNdE_MlY1e8rzoi59O6Rj3FeU1Soe6y5iRhIkMgf9VKRpA2dMTdDuWz9mCj5gD_78L13HjUenL5dAyfjYCxcA").join());
  }
}
