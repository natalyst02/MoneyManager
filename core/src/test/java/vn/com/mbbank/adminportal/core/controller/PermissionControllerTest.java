package vn.com.mbbank.adminportal.core.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import vn.com.mbbank.adminportal.common.ApplicationTest;
import org.springframework.test.web.servlet.MockMvc;
import vn.com.mbbank.adminportal.common.SqlStatementTest;
import vn.com.mbbank.adminportal.common.WithMockPapUser;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PermissionControllerTest extends ApplicationTest {
  @Autowired
  private MockMvc mvc;

  @Sql(statements = SqlStatementTest.PERMISSIONS + """
    truncate table  PAP_ROLE;
    truncate table PAP_USER;
    truncate table PAP_USER_ROLE;
    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES (1, 'quydv2', 'idKeycloakTest','MB123456', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp );
  
    INSERT INTO PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, REASON, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES (1, 'code1', 'role1', 'IT', 'role so 1', '{"user": 15, "role" : 31, "permission" : 15}', 'abc', 1, 'admin', systimestamp, 'admin', systimestamp);
    INSERT INTO PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, REASON, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES (2, 'code2', 'role2', 'IT', 'role so 2', '{"user": 15, "role" : 31, "permission" : 15}', 'abc', 1, 'admin', systimestamp, 'admin', systimestamp);
    INSERT INTO PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES (1, 1, 1, 'system', systimestamp, 'system', systimestamp);
    INSERT INTO PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES (2, 1, 2, 'system', systimestamp, 'system', systimestamp);
  """)
  @WithMockPapUser(username = "quydv2")
  @Test
  public void getAllPermissions_shouldReturnAllPermissions() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/permissions")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data[0].module", is("user")))
        .andExpect(jsonPath("$.data[0].action", is("view")))
        .andExpect(jsonPath("$.data[0].type", is("ADMIN")));
  }

  @Sql(statements = SqlStatementTest.PERMISSIONS + """
        truncate table  PAP_ROLE;
    truncate table PAP_USER;
    truncate table PAP_USER_ROLE;
    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES (1, 'quydv2', 'idKeycloakTest','MB123456', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp );
  
    INSERT INTO PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, REASON, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES (1, 'code1', 'role1', 'IT', 'role so 1', '{"user": 15, "role" : 31, "permission" : 15}', 'abc', 1, 'admin', systimestamp, 'admin', systimestamp);
    INSERT INTO PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, REASON, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES (2, 'code2', 'role2', 'IT', 'role so 2', '{"user": 15, "role" : 31, "permission" : 15}', 'abc', 1, 'admin', systimestamp, 'admin', systimestamp);
    INSERT INTO PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES (1, 1, 1, 'system', systimestamp, 'system', systimestamp);
    INSERT INTO PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES (2, 1, 2, 'system', systimestamp, 'system', systimestamp);
  """)
  @WithMockPapUser(username = "test", permissions = "")
  @Test
  public void getPermissionsByName_HasNoPermission() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/permissions")
            .param("description", "Create")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.soaErrorCode").value("008006-4002"))
        .andExpect(jsonPath("$.soaErrorDesc").value("Access Denied"));
  }
}