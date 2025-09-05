package vn.com.mbbank.adminportal.core.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.jdbc.Sql;
import vn.com.mbbank.adminportal.common.ApplicationTest;
import vn.com.mbbank.adminportal.common.SqlStatementTest;
import vn.com.mbbank.adminportal.common.config.RedisClusterAdapter;
import vn.com.mbbank.adminportal.core.service.internal.PermissionServiceInternal;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class PermissionServiceInternalTest extends ApplicationTest {
  @Autowired
  private PermissionServiceInternal permissionService;
  @Autowired
  private RedisClusterAdapter redisClusterAdapter;
  @Value("${redis.keys.all-permissions}")
  private String allPermissionsKey;
  @Sql(statements = """
      truncate table PAP_ROLE;
      truncate table PAP_USER;
      insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
      values (1, 'ADMIN', 'ADMIN', 'IT', 'Admin role', '{"user": 15, "role": 15, "permission": 1}', 1, 'system', systimestamp, 'system', systimestamp);
      insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL, JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
      values (1, 'quydv2', 'idKeycloakTest', 'MB123456', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp);
      """)
  @Test
  void hasPermission_success() {
    var permission = new HashMap<String, Integer>();
    permission.put("user", 15);
    permission.put("permission", 1);
    permission.put("role", 15);
    assertTrue(permissionService.hasPermission(permission, "user", 1));
  }

  @Sql(statements = """
      truncate table PAP_ROLE;
      truncate table PAP_USER;
      insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
      values (1, 'ADMIN', 'ADMIN', 'IT', 'Admin role', '{"user": 14, "role": 15, "permission": 1}', 1, 'system', systimestamp, 'system', systimestamp);
      insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL, JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
      values (1, 'quydv2', 'idKeycloakTest', 'MB123456', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp);
      """)
  @Test
  void hasPermission_fail() {
    var permission = new HashMap<String, Integer>();
    permission.put("user", 14);
    permission.put("permission", 1);
    permission.put("role", 15);
    assertFalse(permissionService.hasPermission(permission, "user", 1));
  }

  @Sql(statements = SqlStatementTest.PERMISSIONS)
  @Test
  void getAllPermission_success() {
    redisClusterAdapter.delete(allPermissionsKey);
    var permissions = permissionService.getAllPermission();
    assertEquals(48, permissions.size());
    assertNotNull(redisClusterAdapter.get(allPermissionsKey, Object.class));
  }
}
