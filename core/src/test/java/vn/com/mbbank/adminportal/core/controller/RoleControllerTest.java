package vn.com.mbbank.adminportal.core.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import vn.com.mbbank.adminportal.common.ApplicationTest;
import vn.com.mbbank.adminportal.common.SqlStatementTest;
import vn.com.mbbank.adminportal.common.WithMockPapUser;
import vn.com.mbbank.adminportal.common.config.RedisClusterAdapter;
import vn.com.mbbank.adminportal.common.util.Constant;
import vn.com.mbbank.adminportal.core.util.ErrorCode;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RoleControllerTest extends ApplicationTest {

  @Autowired
  private MockMvc mvc;

  @Autowired
  private RedisClusterAdapter redisClusterAdapter;
  @Value("${redis.keys.all-permissions}")
  private String allPermissionsKey;

  @Sql(statements = SqlStatementTest.PERMISSIONS + """
        truncate table  PAP_ROLE;
        truncate table PAP_USER;
        truncate table PAP_USER;

        insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
        values (1, 'quydv2', 'idKeycloakTest','MB123456', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp );
        
        insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
        values (1, 'code1', 'role1', 'IT', 'role so 1', '{"user": 31, "role" : 8, "permission" : 1}', 1, 'admin', systimestamp, 'admin', systimestamp);
        
        insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
        values (2, 'code2', 'role2', 'BUSINESS', 'role so 2', '{"user": 8, "role" : 15, "permission" : 10}', 0, 'admin', systimestamp, 'admin', systimestamp);
       
        insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
        values (3, 'code3', 'role3', 'ADMIN', 'role so 3', '{"user": 8, "role" : 15, "routing-whitelist" : 1}', 1, 'admin', systimestamp, 'admin', systimestamp);
  """)
  @WithMockPapUser(username = "quydv2")
  @Test
  void get_roles_success() throws Exception {
    redisClusterAdapter.delete(allPermissionsKey);
    var request = MockMvcRequestBuilders.get("/roles?page=0&size=10&type=IT&type=ALL")
        .header("Content-Type", "application/json");
    mvc.perform(request)
        .andExpect(status().is2xxSuccessful())
        .andExpect(jsonPath("$.data.content").isNotEmpty())
        .andExpect(jsonPath("$.data.content.size()").value(2))
        .andExpect(jsonPath("$.data.content.[0].permissions.user.size()").value(4))
        .andExpect(jsonPath("$.data.content.[0].permissions.role.size()").value(1))
        .andExpect(jsonPath("$.data.content.[0].permissions.permission.size()").value(1))
        .andExpect(jsonPath("$.data.content.[1].permissions.size()").value(2))
    ;
  }

  @Sql(statements = SqlStatementTest.PERMISSIONS + """
    truncate table  PAP_ROLE;
    truncate table PAP_USER;
    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (1, 'quydv2', 'idKeycloakTest','MB123456', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp );
    
    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (1, 'code1', 'role1', 'IT', 'role so 1', '{"user": 21, "role" : 15, "transaction" : 10}', 1, 'admin', systimestamp, 'admin', systimestamp);
  
    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (2, 'code2', 'role2', 'IT', 'role so 2', '{"user": 8, "role" : 15, "transaction" : 10}', 0, 'admin', systimestamp, 'admin', systimestamp);
 
  """)
  @WithMockPapUser(username = "quydv2")
  @Test
  void get_roles_name_success() throws Exception {
    redisClusterAdapter.delete(allPermissionsKey);
    var request = MockMvcRequestBuilders.get("/roles?name=le1&page=0&size=10")
        .header("Content-Type", "application/json");
    mvc.perform(request)
        .andExpect(status().is2xxSuccessful())
        .andExpect(jsonPath("$.data.content").isNotEmpty())
        .andExpect(jsonPath("$.data.content.size()").value(1));
  }

  @Sql(statements = SqlStatementTest.PERMISSIONS + """
    truncate table  PAP_ROLE;
    truncate table PAP_USER;
    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (1, 'quydv2', 'idKeycloakTest','MB123456', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp );
    
    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (1, 'code1', 'role1', 'IT', 'role so 1', '{"user": 21, "role" : 15, "transaction" : 10}', 1, 'admin', systimestamp, 'admin', systimestamp);
  
    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (2, 'code2', 'role2', 'BUSINESS', 'role so 2', '{"user": 8, "role" : 15, "transaction" : 10}', 0, 'admin', systimestamp, 'admin', systimestamp);
 
  """)
  @WithMockPapUser(username = "quydv2")
  @Test
  void get_roles_type_success() throws Exception {
    var request = MockMvcRequestBuilders.get("/roles?type=BUSINESS&page=0&size=10")
        .header("Content-Type", "application/json");
    mvc.perform(request)
        .andExpect(status().is2xxSuccessful())
        .andExpect(jsonPath("$.data.content").isNotEmpty())
        .andExpect(jsonPath("$.data.content.size()").value(1));
  }

  @Sql(statements = SqlStatementTest.PERMISSIONS + """
    truncate table  PAP_ROLE;
    truncate table PAP_USER;
    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (1, 'quydv2', 'idKeycloakTest','MB123456', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp );
    
    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (1, 'code1', 'role1', 'aaa', 'role so 1', '{"user": "21", "role" : 15, "transaction" : 10}', 1, 'admin', systimestamp, 'admin', systimestamp);
  """)
  @WithMockPapUser(username = "quydv2")
  @Test
  void get_roles_fail_convert_type() throws Exception {
    var request = MockMvcRequestBuilders.get("/roles?type=aaa&page=0&size=10")
        .header("Content-Type", "application/json");
    mvc.perform(request)
        .andExpect(status().is4xxClientError());
  }

  @Sql(statements = SqlStatementTest.PERMISSIONS + """
    truncate table  PAP_ROLE;
    truncate table PAP_USER;
    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (1, 'quydv2', 'idKeycloakTest','MB123456', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp );
    
    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (1, 'code1', 'role1', 'BUSINESS', 'role so 1', '{"user": "21", "role" : 2, "transaction" : 10}', 1, 'admin', systimestamp, 'admin', systimestamp);
  """)
  @WithMockPapUser(username = "quydv2", permissions = "")
  @Test
  void get_roles_access_denied() throws Exception {
    var request = MockMvcRequestBuilders.get("/roles?type=BUSINESS&page=0&size=10")
        .header("Content-Type", "application/json");
    mvc.perform(request)
        .andExpect(status().is4xxClientError())
    ;
  }

  @Sql(statements = SqlStatementTest.PERMISSIONS + """
    truncate table  PAP_ROLE;
    truncate table PAP_USER;
    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (1, 'quydv2', 'idKeycloakTest','MB123456', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp );
    
    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (1, 'code1', 'role1', 'IT', 'role so 1', '{"user": 31, "role" : 31, "transaction" : 31}', 1, 'admin', systimestamp, 'admin', systimestamp);
  
    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (2, 'code2', 'role2', 'BUSINESS', 'role so 2', '{"user": 8, "role" : 15, "transaction" : 10}', 0, 'admin', systimestamp, 'admin', systimestamp);
 
  """)
  @WithMockPapUser(username = "quydv2")
  @Test
  void create_role_success() throws Exception {
    var requestBody = """
        {
            "code": "code3_",
            "type": "IT",
            "description": "role so 3",
            "name": "rol3",
            "reason" : "tạo mới",
            "active": true,
            "permissionIds": [
                1,
                2,
                3,
                4,
                9
            ]
        }
        """;
    var request = MockMvcRequestBuilders.post("/roles")
        .header("Content-Type", "application/json")
        .content(requestBody);
    mvc.perform(request)
        .andExpect(status().is2xxSuccessful())
        .andExpect(jsonPath("$.data.code").value("code3_"))
        .andExpect(jsonPath("$.data.type").value("IT"))
        .andExpect(jsonPath("$.data.description").value("role so 3"))
        .andExpect(jsonPath("$.data.name").value("rol3"))
        .andExpect(jsonPath("$.data.permissions.user").value(15))
        .andExpect(jsonPath("$.data.permissions.role").value(8))
        .andExpect(jsonPath("$.data.reason").value("tạo mới"))
    ;
  }

  @Sql(statements = SqlStatementTest.PERMISSIONS + """
    truncate table  PAP_ROLE;
    truncate table PAP_USER;
    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (1, 'quydv2', 'idKeycloakTest','MB123456', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp );
    
    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (1, 'code1', 'role1', 'IT', 'role so 1', '{"user": 7, "role" : 31, "transaction" : 31}', 1, 'admin', systimestamp, 'admin', systimestamp);
  
    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (2, 'code2', 'role2', 'BUSINESS', 'role so 2', '{"user": 8, "role" : 15, "transaction" : 10}', 0, 'admin', systimestamp, 'admin', systimestamp);
 
  """)
  @WithMockPapUser(username = "quydv2", permissions = """
      {
        "role": 15
      }
      """)
  @Test
  void create_role_permission_invalid() throws Exception {
    var requestBody = """
        {
            "code": "code3",
            "type": "BUSINESS",
            "description": "role so 3",
            "name": "rol3",
            "active": true,
            "reason" : "tạo mới",
            "permissionIds": [
                1,
                2,
                3,
                4,
                9
            ]
        }
        """;
    var request = MockMvcRequestBuilders.post("/roles")
        .header("Content-Type", "application/json")
        .content(requestBody);
    mvc.perform(request)
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.soaErrorCode").value("008006-" + ErrorCode.PERMISSION_NOT_FOUND.code()))
    ;
  }

  @Sql(statements = SqlStatementTest.PERMISSIONS + """
    truncate table  PAP_ROLE;
    truncate table PAP_USER;
    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (1, 'quydv2', 'idKeycloakTest','MB123456', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp );
    
    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (1, 'code1', 'role1', 'IT', 'role so 1', '{"user": 7, "role" : 31, "transaction" : 31}', 1, 'admin', systimestamp, 'admin', systimestamp);
  
    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (2, 'code2', 'role2', 'BUSINESS', 'role so 2', '{"user": 8, "role" : 15, "transaction" : 10}', 0, 'admin', systimestamp, 'admin', systimestamp);
 
  """)
  @WithMockPapUser(username = "quydv2", permissions = """
      {
        "role": 15
      }
      """)
  @Test
  void create_role_code_invalid() throws Exception {
    var requestBody = """
        {
            "code": "code3 ",
            "type": "BUSINESS",
            "description": "role so 3",
            "name": "rol3",
            "reason" : "tạo mới",
            "active": true,
            "permissionIds": [
                1,
                2,
                3,
                4,
                9
            ]
        }
        """;
    var request = MockMvcRequestBuilders.post("/roles")
        .header("Content-Type", "application/json")
        .content(requestBody);
    mvc.perform(request)
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.soaErrorCode").value(Constant.PREFIX_RESPONSE_CODE + ErrorCode.INVALID_PARAMETERS.code()))
    ;
  }

  @Sql(statements = SqlStatementTest.PERMISSIONS + """
    truncate table  PAP_ROLE;
    truncate table PAP_USER;
    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (1, 'quydv2', 'idKeycloakTest','MB123456', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp );
    
    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (1, 'code1', 'role1', 'IT', 'role so 1', '{"user": 7, "role" : 23, "transaction" : 31}', 1, 'admin', systimestamp, 'admin', systimestamp);
  
    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (2, 'code2', 'role2', 'BUSINESS', 'role so 2', '{"user": 8, "role" : 15, "transaction" : 10}', 0, 'admin', systimestamp, 'admin', systimestamp);
 
  """)
  @WithMockPapUser(username = "quydv2", permissions = "")
  @Test
  void create_role_forbidden() throws Exception {
    var requestBody = """
        {
            "code": "code3",
            "type": "BUSINESS",
            "description": "role so 3",
            "name": "rol3",
            "reason" : "tạo mới",
            "active": true,
            "permissionIds": [
                1,
                2,
                3,
                4,
                9
            ]
        }
        """;
    var request = MockMvcRequestBuilders.post("/roles")
        .header("Content-Type", "application/json")
        .content(requestBody);
    mvc.perform(request)
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.soaErrorCode").value("008006-4002"))
    ;
  }

  @WithMockPapUser(username = "quydv2")
  @Test
  void create_role_invalid_parameter() throws Exception {
    var requestBody = """
        {
            "code": "code3",
            "type": "BUSINESS",
            "description": "role so 3",
            "name": "rol3",
            "reason" : "tạo mới",
            "active": true
         
        }
        """;
    var request = MockMvcRequestBuilders.post("/roles")
        .header("Content-Type", "application/json")
        .content(requestBody);
    mvc.perform(request)
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.soaErrorCode").value("008006-4000"))
    ;
  }

  @Sql(statements = SqlStatementTest.PERMISSIONS + """
    truncate table  PAP_ROLE;
    truncate table PAP_USER;
    
    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (1, 'quydv2', 'idKeycloakTest','MB123456', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp );
    
    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (1, 'code1', 'role1', 'IT', 'role so 1', '{"user": 31, "role" : 31, "transaction" : 31}', 1, 'admin', systimestamp, 'admin', systimestamp);
  
    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (2, 'code2', 'role2', 'IT', 'role so 2', '{"user": 8, "role" : 15, "transaction" : 10}', 0, 'admin', systimestamp, 'admin', systimestamp);
 
  """)
  @WithMockPapUser(username = "quydv2")
  @Test
  void update_role_success() throws Exception {
    var requestBody = """
        {
            "description": "role so 2 updated",
            "name": "role 2 updated",
            "active": true,
            "reason" : "tạo mới",
            "permissionIds": [
                1,
                2,
                3,
                4,
                9
            ]
        }
        """;
    var request = MockMvcRequestBuilders.put("/roles/2")
        .header("Content-Type", "application/json")
        .content(requestBody);
    mvc.perform(request)
        .andExpect(status().is2xxSuccessful())
        .andExpect(jsonPath("$.data.code").value("code2"))
        .andExpect(jsonPath("$.data.description").value("role so 2 updated"))
        .andExpect(jsonPath("$.data.name").value("role 2 updated"))
        .andExpect(jsonPath("$.data.permissions.user").value(15))
        .andExpect(jsonPath("$.data.permissions.role").value(16))
        .andExpect(jsonPath("$.data.active").value(true))
        .andExpect(jsonPath("$.data.reason").value("tạo mới"))
    ;
  }

  @Sql(statements = SqlStatementTest.PERMISSIONS + """
    truncate table  PAP_ROLE;
    truncate table PAP_USER;
    
    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (1, 'quydv2', 'idKeycloakTest','MB123456', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp );
    
    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (1, 'code1', 'role1', 'IT', 'role so 1', '{"user": 31, "role" : 31, "transaction" : 31}', 1, 'admin', systimestamp, 'admin', systimestamp);
  
    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (2, 'code2', 'role2', 'ALL', 'role so 2', '{"user": 8, "role" : 15, "transaction" : 10}', 0, 'admin', systimestamp, 'admin', systimestamp);
 
  """)
  @WithMockPapUser(username = "quydv2")
  @Test
  void update_role_admin() throws Exception {
    var requestBody = """
        {
            "description": "role so 2 updated",
            "name": "role 2 updated",
            "active": true,
            "reason" : "tạo mới",
            "permissionIds": [
                1,
                2,
                3,
                4,
                9
            ]
        }
        """;
    var request = MockMvcRequestBuilders.put("/roles/2")
        .header("Content-Type", "application/json")
        .content(requestBody);
    mvc.perform(request)
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.soaErrorCode").value(Constant.PREFIX_RESPONSE_CODE + ErrorCode.ROLE_CANT_BE_UPDATED.code()))
    ;
  }

  @Sql(statements = SqlStatementTest.PERMISSIONS + """
    truncate table  PAP_ROLE;
    truncate table PAP_USER;
    truncate table PAP_USER_ROLE;
    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (1, 'code1', 'role1', 'IT', 'role so 1', '{"user": 7, "role" : 31, "transaction" : 31}', 1, 'admin', systimestamp, 'admin', systimestamp);
  
    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (2, 'code2', 'role2', 'IT', 'role so 2', '{"user": 8, "role" : 15, "transaction" : 10}', 1, 'admin', systimestamp, 'admin', systimestamp);
 
 
    INSERT INTO PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT) 
    VALUES (1, 1, 2, 'system', systimestamp, 'system', systimestamp);

  """)
  @WithMockPapUser(username = "quydv2", permissions = """
      {"role": 15}
      """)
  @Test
  void update_role_permission_invalid() throws Exception {
    var requestBody = """
        {
            "code": "code2",
            "type": "IT",
            "description": "role so 2",
            "name": "role2",
            "reason" : "tạo mới",
            "active": false,
            "permissionIds": [
                1,
                2,
                3,
                4,
                9
            ]
        }
        """;
    var request = MockMvcRequestBuilders.put("/roles/2")
        .header("Content-Type", "application/json")
        .content(requestBody);
    mvc.perform(request)
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.soaErrorCode").value("008006-" + ErrorCode.NON_INFERIOR_ROLE.code()));
  }

  @Sql(statements = """
    truncate table  PAP_ROLE;
    truncate table PAP_USER;
    
    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (1, 'quydv2', 'idKeycloakTest','MB123456', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp );
    
    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (1, 'code1', 'role1', 'IT', 'role so 1', '{"user": 31, "role" : 31, "transaction" : 31}', 1, 'admin', systimestamp, 'admin', systimestamp);
  
    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (2, 'code2', 'role2', 'BUSINESS', 'role so 2', '{"user": 8, "role" : 15, "transaction" : 10}', 1, 'admin', systimestamp, 'admin', systimestamp);
 
  """)
  @WithMockPapUser(username = "quydv2")
  @Test
  void update_role_not_found() throws Exception {
    var requestBody = """
        {
            "type": "BUSINESS",
            "description": "role so 2",
            "name": "role2",
            "reason" : "tạo mới",
            "active": false,
            "permissionIds": [
                1,
                2,
                3,
                4,
                9
            ]
        }
        """;
    var request = MockMvcRequestBuilders.put("/roles/3")
        .header("Content-Type", "application/json")
        .content(requestBody);
    mvc.perform(request)
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.soaErrorCode").value("008006-" + ErrorCode.ROLE_NOT_FOUND.code()))
    ;
  }

  @Sql(statements = SqlStatementTest.PERMISSIONS + """
    truncate table  PAP_ROLE;
    truncate table PAP_USER;
    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (1, 'quydv2', 'idKeycloakTest','MB123456', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp );
    
    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (1, 'code1', 'role1', 'IT', 'role so 1', '{"user": 7, "role" : 31, "transaction" : 31}', 1, 'admin', systimestamp, 'admin', systimestamp);
  
    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (2, 'code2', 'role2', 'BUSINESS', 'role so 2', '{"user": 8, "role" : 15, "transaction" : 10}', 1, 'admin', systimestamp, 'admin', systimestamp);
 
  """)
  @WithMockPapUser(username = "quydv2")
  @Test
  void update_role_param_invalid() throws Exception {
    var requestBody = """
        {
            "code": null,
            "type": null,
            "description": "",
            "name": "",
          
            "permissionIds": null
        }
        """;
    var request = MockMvcRequestBuilders.put("/roles/2")
        .header("Content-Type", "application/json")
        .content(requestBody);
    mvc.perform(request)
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.soaErrorCode").value("008006-4000"))
    ;
  }

  @Sql(statements = SqlStatementTest.PERMISSIONS + """
    truncate table  PAP_ROLE;
    truncate table PAP_USER;
    truncate table PAP_USER_ROLE;
    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (1, 'quydv2', 'idKeycloakTest','MB123456', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp );
    
    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (1, 'code1', 'role1', 'IT', 'role so 1', '{"user": 31, "role" : 31}', 1, 'admin', systimestamp, 'admin', systimestamp);
  
    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (2, 'code2', 'role2', 'IT', 'role so 2', '{"user": 8, "role" : 15}', 0, 'admin', systimestamp, 'admin', systimestamp);
 
    INSERT INTO PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT) 
    VALUES (1, 1, 2, 'system', systimestamp, 'system', systimestamp);
  """)
  @WithMockPapUser(username = "quydv2")
  @Test
  void get_Role_Success() throws Exception {
    redisClusterAdapter.delete(allPermissionsKey);
    var request = MockMvcRequestBuilders.get("/roles/inquiry/2")
        .header("Content-Type", "application/json");
    mvc.perform(request)
        .andExpect(status().is2xxSuccessful())
        .andExpect(jsonPath("$.data").isNotEmpty())
        .andExpect(jsonPath("$.data.permissions.size()").value(16))
        .andExpect(jsonPath("$.data.permissions.role.size()").value(5))
        .andExpect(jsonPath("$.data.permissions.permission.size()").value(1))
        .andExpect(jsonPath("$.data.permissions.user.size()").value(5))

        .andExpect(jsonPath("$.data.permissions.role.[0].selected").value(true))
        .andExpect(jsonPath("$.data.permissions.role.[1].selected").value(true))
        .andExpect(jsonPath("$.data.permissions.role.[2].selected").value(true))
        .andExpect(jsonPath("$.data.permissions.role.[3].selected").value(true))
        .andExpect(jsonPath("$.data.permissions.role.[4].selected").value(false))

        .andExpect(jsonPath("$.data.permissions.permission.[0].selected").value(false))

        .andExpect(jsonPath("$.data.permissions.user.[0].selected").value(false))
        .andExpect(jsonPath("$.data.permissions.user.[1].selected").value(false))
        .andExpect(jsonPath("$.data.permissions.user.[2].selected").value(false))
        .andExpect(jsonPath("$.data.permissions.user.[3].selected").value(true))
        .andExpect(jsonPath("$.data.permissions.user.[4].selected").value(false))
    ;
  }

  @Sql(statements = SqlStatementTest.PERMISSIONS + """
    truncate table  PAP_ROLE;
    truncate table PAP_USER;
    truncate table PAP_USER_ROLE;

    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (1, 'quydv2', 'idKeycloakTest','MB123456', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp );
    
    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (1, 'code1', 'role1', 'IT', 'role so 1', '{"user": 31, "role" : 31}', 1, 'admin', systimestamp, 'admin', systimestamp);
  
    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (2, 'code2', 'role2', 'IT', 'role so 2', null , 0, 'admin', systimestamp, 'admin', systimestamp);
 
    INSERT INTO PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT) 
    VALUES (1, 1, 1, 'system', systimestamp, 'system', systimestamp);
  """)
  @WithMockPapUser(username = "quydv2")
  @Test
  void get_Role_Permission_Empty() throws Exception {
    redisClusterAdapter.delete(allPermissionsKey);
    var request = MockMvcRequestBuilders.get("/roles/inquiry/2")
        .header("Content-Type", "application/json");
    mvc.perform(request)
        .andExpect(status().is2xxSuccessful())
        .andExpect(jsonPath("$.data").isNotEmpty())

        .andExpect(jsonPath("$.data.permissions.role.[0].selected").value(false))
        .andExpect(jsonPath("$.data.permissions.role.[1].selected").value(false))
        .andExpect(jsonPath("$.data.permissions.role.[2].selected").value(false))
        .andExpect(jsonPath("$.data.permissions.role.[3].selected").value(false))

        .andExpect(jsonPath("$.data.permissions.permission.[0].selected").value(false))

        .andExpect(jsonPath("$.data.permissions.user.[0].selected").value(false))
        .andExpect(jsonPath("$.data.permissions.user.[1].selected").value(false))
        .andExpect(jsonPath("$.data.permissions.user.[2].selected").value(false))
        .andExpect(jsonPath("$.data.permissions.user.[3].selected").value(false))
    ;
  }

  @Sql(statements = SqlStatementTest.PERMISSIONS + """
    truncate table  PAP_ROLE;
    truncate table PAP_USER;
    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (1, 'quydv2', 'idKeycloakTest','MB123456', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp );
    
    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (1, 'code1', 'role1', 'IT', 'role so 1', '{"user": 31, "role" : 31}', 1, 'admin', systimestamp, 'admin', systimestamp);
  
    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (2, 'code2', 'role2', 'BUSINESS', 'role so 2', null , 0, 'admin', systimestamp, 'admin', systimestamp);
 
  """)
  @WithMockPapUser(username = "quydv2")
  @Test
  void get_Role_Positive_Id() throws Exception {
    var request = MockMvcRequestBuilders.get("/roles/inquiry/-2")
        .header("Content-Type", "application/json");
    mvc.perform(request)
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.soaErrorCode").value("008006-4000"))
    ;
  }

  @Sql(statements = SqlStatementTest.PERMISSIONS + """
    truncate table  PAP_ROLE;
    truncate table PAP_USER;
    
    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (1, 'quydv2', 'idKeycloakTest','MB123456', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp );
    
    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (1, 'code1', 'role1', 'IT', 'role so 1', '{"user": 31, "role" : 31}', 1, 'admin', systimestamp, 'admin', systimestamp);
  
    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (2, 'code2', 'role2', 'BUSINESS', 'role so 2', null , 0, 'admin', systimestamp, 'admin', systimestamp);
 
  """)
  @WithMockPapUser(username = "quydv2")
  @Test
  void get_Role_Not_Found() throws Exception {
    var request = MockMvcRequestBuilders.get("/roles/inquiry/20")
        .header("Content-Type", "application/json");
    mvc.perform(request)
        .andExpect(status().is4xxClientError())
        .andExpect(jsonPath("$.soaErrorCode").value("008006-" + ErrorCode.ROLE_NOT_FOUND.code()))
    ;
  }
}