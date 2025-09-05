package vn.com.mbbank.adminportal.core.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import vn.com.mbbank.adminportal.common.ApplicationTest;
import vn.com.mbbank.adminportal.core.service.internal.RoleServiceInternal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RoleServiceInternalTest extends ApplicationTest {
  @Autowired
  private RoleServiceInternal roleService;

  @Sql(statements = """
      truncate table PAP_ROLE;
      insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
      values (1, 'ADMIN', 'ADMIN', 'IT', 'Admin role', '{"user": 15, "role": 15, "permission": 15}', 1, 'system', systimestamp, 'system', systimestamp);
      """)
  @Test
  void getRole_success() {
    var role = roleService.getActiveRoleById(1L);
    assertEquals(role.getPermissions().size(), 3);
    assertEquals(role.getPermissions().get("user"), 15);
  }

  @Sql(statements = """
    truncate table PAP_ROLE;
    truncate table PAP_USER_ROLE;
    INSERT INTO PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, REASON, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT) VALUES (1, 'code1', 'role1', 'IT', 'role so 1', '{"user": 15, "role" : 15, "transaction" : 10}', 'abc', 1, 'admin', systimestamp, 'admin', systimestamp);
    INSERT INTO PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, REASON, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT) VALUES (2, 'code2', 'role2', 'IT', 'role so 2', '{"user": 4, "role" : 15, "transaction" : 10}', 'abc', 1, 'admin', systimestamp, 'admin', systimestamp);
    INSERT INTO PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT) VALUES (1, 1, 1, 'system', systimestamp, 'system', systimestamp);
    INSERT INTO PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT) VALUES (2, 1, 2, 'system', systimestamp, 'system', systimestamp);
""")
  @Test
  void getRolesPermissions_success() {
    var rolesPermissions = roleService.getRolesPermissionsByActiveRole(1L);
    assertEquals(2, rolesPermissions.size());
  }
}
