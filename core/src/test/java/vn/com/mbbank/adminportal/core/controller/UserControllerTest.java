package vn.com.mbbank.adminportal.core.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import vn.com.mbbank.adminportal.common.ApplicationTest;
import vn.com.mbbank.adminportal.common.SqlStatementTest;
import vn.com.mbbank.adminportal.common.WithMockPapUser;
import vn.com.mbbank.adminportal.common.util.Constant;
import vn.com.mbbank.adminportal.core.model.Action;
import vn.com.mbbank.adminportal.core.model.RoleInfo;
import vn.com.mbbank.adminportal.core.model.entity.User;
import vn.com.mbbank.adminportal.core.model.entity.UserRoleHistory;
import vn.com.mbbank.adminportal.core.repository.UserRepository;
import vn.com.mbbank.adminportal.core.repository.UserRoleHistoryRepository;
import vn.com.mbbank.adminportal.core.service.UserService;
import vn.com.mbbank.adminportal.core.service.internal.UserRoleServiceInternal;
import vn.com.mbbank.adminportal.core.thirdparty.keycloak.KeycloakInternalClient;
import vn.com.mbbank.adminportal.core.util.ErrorCode;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static vn.com.mbbank.adminportal.core.util.ErrorCode.*;

class UserControllerTest extends ApplicationTest {

    @Autowired
    private MockMvc mvc;

    @SpyBean
    private UserRoleServiceInternal userRoleService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleHistoryRepository userRoleHistoryRepository;
    @SpyBean
    private KeycloakInternalClient keycloakInternalClient;
    @SpyBean
    private UserService userService;

    @Sql(statements = """
    truncate table PAP_USER;
   insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
      values(2, 'tunghv', 'adae07d5-7e90-4850-81fe-8c0a421c688f', 'MB123456', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 0,'system' , systimestamp, 'system', systimestamp,'admin');

    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (1, 'tuanna', 'idKeycloakTest2','MB1234567', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp,'admin' );
  """)
    @WithMockPapUser(username = "tuanna")
    @Test

    void update_Active_Success() throws Exception {
        var requestBody = """
        {
            "reason": "CNTT",
            "active": true
        }
        """;
        var request = MockMvcRequestBuilders.put("/users/2")
            .header("Content-Type", "application/json")
            .content(requestBody);
        mvc.perform(request)
            .andExpect(status().is2xxSuccessful())
            .andExpect(jsonPath("$.data.reason").value("CNTT"))
            .andExpect(jsonPath("$.data.active").value(true))
        ;

    }
    @Sql(statements = """
    truncate table PAP_USER;
      insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
        values(2, 'thangnd', 'cf1bfc7e-9126-4884-8cb0-ce645c821a45', 'MB123456', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1,'system' , systimestamp, 'system', systimestamp,'admin' );
        insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
        values (1, 'tuanna', 'idKeycloakTest2','MB1234567', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp,'admin' );

  """)
    @WithMockPapUser(username = "tuanna")
    @Test
    void update_InActive_success() throws Exception {
        var requestBody = """
        {
            "reason": "CNTT2",
            "active": false
        }
        """;
        var request = MockMvcRequestBuilders.put("/users/2")
            .header("Content-Type", "application/json")
            .content(requestBody);
        mvc.perform(request)
            .andExpect(status().is2xxSuccessful())
            .andExpect(jsonPath("$.data.reason").value("CNTT2"))
            .andExpect(jsonPath("$.data.active").value(false))
        ;

    }
    @Sql(statements = """
    truncate table PAP_USER;
    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (1, 'quydv2', 'idKeycloakTest','MB123456', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 0, 'system', systimestamp, 'system', systimestamp,'admin' );

    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (2, 'tuanna', 'idKeycloakTest2','MB1234567', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp,'admin' );


  """)
    @WithMockPapUser(username = "quydv2")
    @Test
    void update_NotFoundUser() throws Exception {
        var requestBody = """
        {
            "reason": "CNTT2",
            "active": false
        }
        """;
        var request = MockMvcRequestBuilders.put("/users/3")
            .header("Content-Type", "application/json")
            .content(requestBody);
        mvc.perform(request)
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.soaErrorCode", equalTo(Constant.PREFIX_RESPONSE_CODE + USER_NOT_FOUND.code())))

        ;

    }

    @Sql(statements = """
    truncate table PAP_USER;
    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME,ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (1, 'quydv2', 'idKeycloakTest','MB123456', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT' ,0, 'system', systimestamp, 'system', systimestamp,'admin' );

    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (2, 'tuanna', 'idKeycloakTest2','MB1234567', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp,'admin' );
  """)
    @WithMockPapUser(username = "quydv2")
    @Test

    void update_Deactive_Themselves_Failed() throws Exception {
        var requestBody = """
        {
            "reason": "CNTT",
            "active": false
        }
        """;
        var request = MockMvcRequestBuilders.put("/users/1")
            .header("Content-Type", "application/json")
            .content(requestBody);
        mvc.perform(request)
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.soaErrorCode", equalTo(Constant.PREFIX_RESPONSE_CODE + SELF_DEACTIVATE_ERROR.code())))
        ;

    }

    @Sql(statements = """
    truncate table PAP_USER;
    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME,ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (1, 'quydv2', 'idKeycloakTest','MB123456', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT' ,0, 'system', systimestamp, 'system', systimestamp,'admin' );

    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (2, 'tuanna', 'idKeycloakTest2','MB1234567', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp,'admin' );
  """)
    @WithMockPapUser(username = "quydv2")
    @Test

    void update_Active_And_RoleIds_Null() throws Exception {
        var requestBody = """
        {
            "reason": "CNTT"
        }
        """;
        var request = MockMvcRequestBuilders.put("/users/1")
            .header("Content-Type", "application/json")
            .content(requestBody);
        mvc.perform(request)
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.soaErrorCode", equalTo(Constant.PREFIX_RESPONSE_CODE + INVALID_PARAMETERS.code())))
        ;

    }

    @Sql(statements = """
    truncate table  PAP_USER;

     insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (100, 'quydv2', 'idKeycloakTest','MB1234590', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 0, 'system', systimestamp, 'system', systimestamp,'admin' );

    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (200, 'tuanna', 'idKeycloakTest2','MB1234599', 'Tuan', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp,'admin' );

        truncate table PAP_ROLE;

    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (100, 'code1', 'role1', 'IT', 'role so 1', '{"user": 21, "role" : 15, "transaction" : 10}', 1, 'admin', systimestamp, 'admin', systimestamp,'admin');

    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (200, 'code2', 'role2', 'BUSINESS', 'role so 2', '{"user": 8, "role" : 15, "transaction" : 10}', 0, 'admin', systimestamp, 'admin', systimestamp,'admin');

  truncate table PAP_USER_ROLE;
    insert into PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (100, '100','100', 'system', systimestamp, 'system', systimestamp );
    insert into PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (200, '100','200', 'system', systimestamp, 'system', systimestamp );
    insert into PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (300, '200','100', 'system', systimestamp, 'system', systimestamp );

  """)
    @WithMockPapUser(username = "quydv2")
    @Test
    void get_Users_Success() throws Exception {
        var request = MockMvcRequestBuilders.get("/users?page=0&size=10")
            .header("Content-Type", "application/json");
        mvc.perform(request)
            .andExpect(status().is2xxSuccessful())
            .andExpect(jsonPath("$.data.content").isNotEmpty())
            .andExpect(jsonPath("$.data.content.[0].id").value(100))
            .andExpect(jsonPath("$.data.content.[0].username").value("quydv2"))
            .andExpect(jsonPath("$.data.content.[0].employeeCode").value("MB1234590"))
            .andExpect(jsonPath("$.data.content.[0].fullName").value("Quy"))
            .andExpect(jsonPath("$.data.content.[0].phoneNumber").value("0987654321"))
            .andExpect(jsonPath("$.data.content.[0].email").value("test@mail.com"))
            .andExpect(jsonPath("$.data.content.[0].jobName").value("Chuyên viên"))
            .andExpect(jsonPath("$.data.content.[0].orgName").value("Khối CNTT"))
            .andExpect(jsonPath("$.data.content.[0].reason").value("admin"))
            .andExpect(jsonPath("$.data.content.[0].active").value(false))
            .andExpect(jsonPath("$.data.content.[0].createdBy").value("system"))
            .andExpect(jsonPath("$.data.content.[0].updatedBy").value("system"))
            .andExpect(jsonPath("$.data.content.[0].roles.size()").value(2))
            .andExpect(jsonPath("$.data.content.[0].roles.[0].roleId").value(100))
            .andExpect(jsonPath("$.data.content.[0].roles.[0].roleCode").value("code1"))
            .andExpect(jsonPath("$.data.content.[0].roles.[1].roleId").value(200))
            .andExpect(jsonPath("$.data.content.[0].roles.[1].roleCode").value("code2"))

            .andExpect(jsonPath("$.data.content.[1].username").value("tuanna"))
            .andExpect(jsonPath("$.data.content.[1].employeeCode").value("MB1234599"))
            .andExpect(jsonPath("$.data.content.[1].fullName").value("Tuan"))
            .andExpect(jsonPath("$.data.content.[1].phoneNumber").value("0987654321"))
            .andExpect(jsonPath("$.data.content.[1].email").value("test@mail.com"))
            .andExpect(jsonPath("$.data.content.[1].jobName").value("Chuyên viên"))
            .andExpect(jsonPath("$.data.content.[1].orgName").value("Khối CNTT"))
            .andExpect(jsonPath("$.data.content.[1].reason").value("admin"))
            .andExpect(jsonPath("$.data.content.[1].active").value(true))
            .andExpect(jsonPath("$.data.content.[1].createdBy").value("system"))
            .andExpect(jsonPath("$.data.content.[1].updatedBy").value("system"))
            .andExpect(jsonPath("$.data.content.[1].roles.size()").value(1))
            .andExpect(jsonPath("$.data.content.[1].roles.[0].roleId").value(100))
            .andExpect(jsonPath("$.data.content.[1].roles.[0].roleCode").value("code1"));

    }
    @Sql(statements = """
    truncate table  PAP_USER;

     insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (100, 'quydv2', 'idKeycloakTest','MB1234590', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 0, 'system', systimestamp, 'system', systimestamp,'admin' );

    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (200, 'tuanna', 'idKeycloakTest2','MB1234599', 'Tuan', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp,'admin' );

        truncate table PAP_ROLE;

    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (100, 'code1', 'role1', 'IT', 'role so 1', '{"user": 21, "role" : 15, "transaction" : 10}', 1, 'admin', systimestamp, 'admin', systimestamp,'admin');

    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (200, 'code2', 'role2', 'BUSINESS', 'role so 2', '{"user": 8, "role" : 15, "transaction" : 10}', 0, 'admin', systimestamp, 'admin', systimestamp,'admin');

  truncate table PAP_USER_ROLE;
    insert into PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (100, '100','100', 'system', systimestamp, 'system', systimestamp );
    insert into PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (200, '100','200', 'system', systimestamp, 'system', systimestamp );
    insert into PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (300, '200','100', 'system', systimestamp, 'system', systimestamp );

  """)
    @WithMockPapUser(username = "quydv2")
    @Test
    void get_Users_With_Active() throws Exception {
        var request = MockMvcRequestBuilders.get("/users?active=true&page=0&size=10")
            .header("Content-Type", "application/json");
        mvc.perform(request)
            .andExpect(jsonPath("$.data.content.[0].username").value("tuanna"))
            .andExpect(jsonPath("$.data.content.[0].employeeCode").value("MB1234599"))
            .andExpect(jsonPath("$.data.content.[0].fullName").value("Tuan"))
            .andExpect(jsonPath("$.data.content.[0].phoneNumber").value("0987654321"))
            .andExpect(jsonPath("$.data.content.[0].email").value("test@mail.com"))
            .andExpect(jsonPath("$.data.content.[0].jobName").value("Chuyên viên"))
            .andExpect(jsonPath("$.data.content.[0].orgName").value("Khối CNTT"))
            .andExpect(jsonPath("$.data.content.[0].reason").value("admin"))
            .andExpect(jsonPath("$.data.content.[0].active").value(true))
            .andExpect(jsonPath("$.data.content.[0].createdBy").value("system"))
            .andExpect(jsonPath("$.data.content.[0].updatedBy").value("system"))
            .andExpect(jsonPath("$.data.content.[0].roles.size()").value(1))
            .andExpect(jsonPath("$.data.content.[0].roles.[0].roleId").value(100))
            .andExpect(jsonPath("$.data.content.[0].roles.[0].roleCode").value("code1"));
    }

    @Sql(statements = """
    truncate table  PAP_USER;

     insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (100, 'quydv2', 'idKeycloakTest','MB1234590', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 0, 'system', systimestamp, 'system', systimestamp,'admin' );

    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (200, 'tuanna', 'idKeycloakTest2','MB1234599', 'Tuan', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp,'admin' );

        truncate table PAP_ROLE;
   
    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (100, 'code1', 'role1', 'IT', 'role so 1', '{"user": 21, "role" : 15, "transaction" : 10}', 1, 'admin', systimestamp, 'admin', systimestamp,'admin');
  
    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (200, 'code2', 'role2', 'BUSINESS', 'role so 2', '{"user": 8, "role" : 15, "transaction" : 10}', 0, 'admin', systimestamp, 'admin', systimestamp,'admin');
 
  truncate table PAP_USER_ROLE;
    insert into PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (100, '100','100', 'system', systimestamp, 'system', systimestamp );
    insert into PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (200, '100','200', 'system', systimestamp, 'system', systimestamp );
    insert into PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (300, '200','100', 'system', systimestamp, 'system', systimestamp );

  """)
    @WithMockPapUser(username = "quydv2")
    @Test
    void get_Users_With_Username() throws Exception {
        var request = MockMvcRequestBuilders.get("/users?username=quydv&page=0&size=10")
            .header("Content-Type", "application/json");
        mvc.perform(request)
            .andExpect(status().is2xxSuccessful())
            .andExpect(jsonPath("$.data.content").isNotEmpty())
            .andExpect(jsonPath("$.data.content.[0].id").value(100))
            .andExpect(jsonPath("$.data.content.[0].username").value("quydv2"))
            .andExpect(jsonPath("$.data.content.[0].employeeCode").value("MB1234590"))
            .andExpect(jsonPath("$.data.content.[0].fullName").value("Quy"))
            .andExpect(jsonPath("$.data.content.[0].phoneNumber").value("0987654321"))
            .andExpect(jsonPath("$.data.content.[0].email").value("test@mail.com"))
            .andExpect(jsonPath("$.data.content.[0].jobName").value("Chuyên viên"))
            .andExpect(jsonPath("$.data.content.[0].orgName").value("Khối CNTT"))
            .andExpect(jsonPath("$.data.content.[0].reason").value("admin"))
            .andExpect(jsonPath("$.data.content.[0].active").value(false))
            .andExpect(jsonPath("$.data.content.[0].createdBy").value("system"))
            .andExpect(jsonPath("$.data.content.[0].updatedBy").value("system"))
            .andExpect(jsonPath("$.data.content.[0].roles.size()").value(2))
            .andExpect(jsonPath("$.data.content.[0].roles.[0].roleId").value(100))
            .andExpect(jsonPath("$.data.content.[0].roles.[0].roleCode").value("code1"))
            .andExpect(jsonPath("$.data.content.[0].roles.[1].roleId").value(200))
            .andExpect(jsonPath("$.data.content.[0].roles.[1].roleCode").value("code2"));
    }
    @Sql(statements = """
    truncate table  PAP_USER;

     insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (100, 'quydv2', 'idKeycloakTest','MB1234590', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 0, 'system', systimestamp, 'system', systimestamp,'admin' );

    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (200, 'tuanna', 'idKeycloakTest2','MB1234599', 'Tuan', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp,'admin' );

        truncate table PAP_ROLE;
   
    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (100, 'code1', 'role1', 'IT', 'role so 1', '{"user": 21, "role" : 15, "transaction" : 10}', 1, 'admin', systimestamp, 'admin', systimestamp,'admin');
  
    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (200, 'code2', 'role2', 'BUSINESS', 'role so 2', '{"user": 8, "role" : 15, "transaction" : 10}', 0, 'admin', systimestamp, 'admin', systimestamp,'admin');
 
  truncate table PAP_USER_ROLE;
    insert into PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (100, '100','100', 'system', systimestamp, 'system', systimestamp );
    insert into PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (200, '100','200', 'system', systimestamp, 'system', systimestamp );
    insert into PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (300, '200','100', 'system', systimestamp, 'system', systimestamp );

  """)
    @WithMockPapUser(username = "quydv2")
    @Test
    void get_Users_With_Fullname() throws Exception {
        var request = MockMvcRequestBuilders.get("/users?fullName=Tuan&page=0&size=10")
            .header("Content-Type", "application/json");
        mvc.perform(request)
            .andExpect(status().is2xxSuccessful())
            .andExpect(jsonPath("$.data.content.[0].username").value("tuanna"))
            .andExpect(jsonPath("$.data.content.[0].employeeCode").value("MB1234599"))
            .andExpect(jsonPath("$.data.content.[0].fullName").value("Tuan"))
            .andExpect(jsonPath("$.data.content.[0].phoneNumber").value("0987654321"))
            .andExpect(jsonPath("$.data.content.[0].email").value("test@mail.com"))
            .andExpect(jsonPath("$.data.content.[0].jobName").value("Chuyên viên"))
            .andExpect(jsonPath("$.data.content.[0].orgName").value("Khối CNTT"))
            .andExpect(jsonPath("$.data.content.[0].reason").value("admin"))
            .andExpect(jsonPath("$.data.content.[0].active").value(true))
            .andExpect(jsonPath("$.data.content.[0].createdBy").value("system"))
            .andExpect(jsonPath("$.data.content.[0].updatedBy").value("system"))
            .andExpect(jsonPath("$.data.content.[0].roles.size()").value(1))
            .andExpect(jsonPath("$.data.content.[0].roles.[0].roleId").value(100))
            .andExpect(jsonPath("$.data.content.[0].roles.[0].roleCode").value("code1"));
    }
    @Sql(statements = """
    truncate table  PAP_USER;

     insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (100, 'quydv2', 'idKeycloakTest','MB1234590', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 0, 'system', systimestamp, 'system', systimestamp,'admin' );

    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (200, 'tuanna', 'idKeycloakTest2','MB1234599', 'Tuan', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp,'admin' );

        truncate table PAP_ROLE;

    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (100, 'code1', 'role1', 'IT', 'role so 1', '{"user": 21, "role" : 15, "transaction" : 10}', 1, 'admin', systimestamp, 'admin', systimestamp,'admin');

    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (200, 'code2', 'role2', 'BUSINESS', 'role so 2', '{"user": 8, "role" : 15, "transaction" : 10}', 0, 'admin', systimestamp, 'admin', systimestamp,'admin');

  truncate table PAP_USER_ROLE;
    insert into PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (100, '100','100', 'system', systimestamp, 'system', systimestamp );
    insert into PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (200, '100','200', 'system', systimestamp, 'system', systimestamp );
    insert into PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (300, '200','100', 'system', systimestamp, 'system', systimestamp );

  """)
    @WithMockPapUser(username = "quydv2")
    @Test
    void get_Users_With_Phone_Number() throws Exception {
        var request = MockMvcRequestBuilders.get("/users?phoneNumber=0987654321&page=0&size=10")
            .header("Content-Type", "application/json");
        mvc.perform(request)
            .andExpect(status().is2xxSuccessful())
            .andExpect(jsonPath("$.data.content").isNotEmpty())
            .andExpect(jsonPath("$.data.content.[0].id").value(100))
            .andExpect(jsonPath("$.data.content.[0].username").value("quydv2"))
            .andExpect(jsonPath("$.data.content.[0].employeeCode").value("MB1234590"))
            .andExpect(jsonPath("$.data.content.[0].fullName").value("Quy"))
            .andExpect(jsonPath("$.data.content.[0].phoneNumber").value("0987654321"))
            .andExpect(jsonPath("$.data.content.[0].email").value("test@mail.com"))
            .andExpect(jsonPath("$.data.content.[0].jobName").value("Chuyên viên"))
            .andExpect(jsonPath("$.data.content.[0].orgName").value("Khối CNTT"))
            .andExpect(jsonPath("$.data.content.[0].reason").value("admin"))
            .andExpect(jsonPath("$.data.content.[0].active").value(false))
            .andExpect(jsonPath("$.data.content.[0].createdBy").value("system"))
            .andExpect(jsonPath("$.data.content.[0].updatedBy").value("system"))
            .andExpect(jsonPath("$.data.content.[0].roles.size()").value(2))
            .andExpect(jsonPath("$.data.content.[0].roles.[0].roleId").value(100))
            .andExpect(jsonPath("$.data.content.[0].roles.[0].roleCode").value("code1"))
            .andExpect(jsonPath("$.data.content.[0].roles.[1].roleId").value(200))
            .andExpect(jsonPath("$.data.content.[0].roles.[1].roleCode").value("code2"))

            .andExpect(jsonPath("$.data.content.[1].username").value("tuanna"))
            .andExpect(jsonPath("$.data.content.[1].employeeCode").value("MB1234599"))
            .andExpect(jsonPath("$.data.content.[1].fullName").value("Tuan"))
            .andExpect(jsonPath("$.data.content.[1].phoneNumber").value("0987654321"))
            .andExpect(jsonPath("$.data.content.[1].email").value("test@mail.com"))
            .andExpect(jsonPath("$.data.content.[1].jobName").value("Chuyên viên"))
            .andExpect(jsonPath("$.data.content.[1].orgName").value("Khối CNTT"))
            .andExpect(jsonPath("$.data.content.[1].reason").value("admin"))
            .andExpect(jsonPath("$.data.content.[1].active").value(true))
            .andExpect(jsonPath("$.data.content.[1].createdBy").value("system"))
            .andExpect(jsonPath("$.data.content.[1].updatedBy").value("system"))
            .andExpect(jsonPath("$.data.content.[1].roles.size()").value(1))
            .andExpect(jsonPath("$.data.content.[1].roles.[0].roleId").value(100))
            .andExpect(jsonPath("$.data.content.[1].roles.[0].roleCode").value("code1"));
    }
    @Sql(statements = """
    truncate table  PAP_USER;

     insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (100, 'quydv2', 'idKeycloakTest','MB1234590', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 0, 'system', systimestamp, 'system', systimestamp,'admin' );

    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (200, 'tuanna', 'idKeycloakTest2','MB1234599', 'Tuan', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp,'admin' );

        truncate table PAP_ROLE;

    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (100, 'code1', 'role1', 'IT', 'role so 1', '{"user": 21, "role" : 15, "transaction" : 10}', 1, 'admin', systimestamp, 'admin', systimestamp,'admin');

    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (200, 'code2', 'role2', 'BUSINESS', 'role so 2', '{"user": 8, "role" : 15, "transaction" : 10}', 0, 'admin', systimestamp, 'admin', systimestamp,'admin');

  truncate table PAP_USER_ROLE;
    insert into PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (100, '100','100', 'system', systimestamp, 'system', systimestamp );
    insert into PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (200, '100','200', 'system', systimestamp, 'system', systimestamp );
    insert into PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (300, '200','100', 'system', systimestamp, 'system', systimestamp );

  """)
    @WithMockPapUser(username = "quydv2")
    @Test
    void get_Users_With_RoleCode() throws Exception {
        var request = MockMvcRequestBuilders.get("/users?roleCode=code&page=0&size=10")
            .header("Content-Type", "application/json");
        mvc.perform(request)
            .andExpect(status().is2xxSuccessful())
            .andExpect(jsonPath("$.data.content").isNotEmpty())
            .andExpect(jsonPath("$.data.content.[0].id").value(100))
            .andExpect(jsonPath("$.data.content.[0].username").value("quydv2"))
            .andExpect(jsonPath("$.data.content.[0].employeeCode").value("MB1234590"))
            .andExpect(jsonPath("$.data.content.[0].fullName").value("Quy"))
            .andExpect(jsonPath("$.data.content.[0].phoneNumber").value("0987654321"))
            .andExpect(jsonPath("$.data.content.[0].email").value("test@mail.com"))
            .andExpect(jsonPath("$.data.content.[0].jobName").value("Chuyên viên"))
            .andExpect(jsonPath("$.data.content.[0].orgName").value("Khối CNTT"))
            .andExpect(jsonPath("$.data.content.[0].reason").value("admin"))
            .andExpect(jsonPath("$.data.content.[0].active").value(false))
            .andExpect(jsonPath("$.data.content.[0].createdBy").value("system"))
            .andExpect(jsonPath("$.data.content.[0].updatedBy").value("system"))

            .andExpect(jsonPath("$.data.content.[1].username").value("tuanna"))
            .andExpect(jsonPath("$.data.content.[1].employeeCode").value("MB1234599"))
            .andExpect(jsonPath("$.data.content.[1].fullName").value("Tuan"))
            .andExpect(jsonPath("$.data.content.[1].phoneNumber").value("0987654321"))
            .andExpect(jsonPath("$.data.content.[1].email").value("test@mail.com"))
            .andExpect(jsonPath("$.data.content.[1].jobName").value("Chuyên viên"))
            .andExpect(jsonPath("$.data.content.[1].orgName").value("Khối CNTT"))
            .andExpect(jsonPath("$.data.content.[1].reason").value("admin"))
            .andExpect(jsonPath("$.data.content.[1].active").value(true))
            .andExpect(jsonPath("$.data.content.[1].createdBy").value("system"))
            .andExpect(jsonPath("$.data.content.[1].updatedBy").value("system"));

    }

    @Sql(statements = """
    truncate table  PAP_USER;

     insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (100, 'quydv2', 'idKeycloakTest','MB1234590', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 0, 'system', systimestamp, 'system', systimestamp,'admin' );

    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (200, 'tuanna', 'idKeycloakTest2','MB1234599', 'Tuan', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp,'admin' );

        truncate table PAP_ROLE;

    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (100, 'code1', 'admin', 'IT', 'role so 1', '{"user": 21, "role" : 15, "transaction" : 10}', 1, 'admin', systimestamp, 'admin', systimestamp,'admin');

    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (200, 'code2', 'opn', 'BUSINESS', 'role so 2', '{"user": 8, "role" : 15, "transaction" : 10}', 0, 'admin', systimestamp, 'admin', systimestamp,'admin');

  truncate table PAP_USER_ROLE;
    insert into PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (100, '100','100', 'system', systimestamp, 'system', systimestamp );
    insert into PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (200, '100','200', 'system', systimestamp, 'system', systimestamp );
    insert into PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (300, '200','100', 'system', systimestamp, 'system', systimestamp );

  """)
    @WithMockPapUser(username = "quydv2")
    @Test
    void get_Users_With_RoleName() throws Exception {
        var request = MockMvcRequestBuilders.get("/users?roleName=op&page=0&size=10")
            .header("Content-Type", "application/json");
        mvc.perform(request)
            .andExpect(status().is2xxSuccessful())
            .andExpect(jsonPath("$.data.content").isNotEmpty())
            .andExpect(jsonPath("$.data.content.size()").value(1))
            .andExpect(jsonPath("$.data.content.[0].id").value(100))
            .andExpect(jsonPath("$.data.content.[0].username").value("quydv2"))
            .andExpect(jsonPath("$.data.content.[0].employeeCode").value("MB1234590"))
            .andExpect(jsonPath("$.data.content.[0].fullName").value("Quy"))
            .andExpect(jsonPath("$.data.content.[0].phoneNumber").value("0987654321"))
            .andExpect(jsonPath("$.data.content.[0].email").value("test@mail.com"))
            .andExpect(jsonPath("$.data.content.[0].jobName").value("Chuyên viên"))
            .andExpect(jsonPath("$.data.content.[0].orgName").value("Khối CNTT"))
            .andExpect(jsonPath("$.data.content.[0].reason").value("admin"))
            .andExpect(jsonPath("$.data.content.[0].active").value(false))
            .andExpect(jsonPath("$.data.content.[0].createdBy").value("system"))
            .andExpect(jsonPath("$.data.content.[0].updatedBy").value("system"))
            .andExpect(jsonPath("$.data.content.[0].roles.[0].roleId").value(200))
            .andExpect(jsonPath("$.data.content.[0].roles.[0].roleCode").value("code2"))
            .andExpect(jsonPath("$.data.content.[0].roles.[0].roleName").value("opn"));

    }

    @Sql(statements = """
    truncate table  PAP_USER;

     insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (100, 'quydv2', 'idKeycloakTest','MB1234590', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 0, 'system', systimestamp, 'system', systimestamp,'admin' );

    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (200, 'tuanna', 'idKeycloakTest2','MB1234599', 'Tuan', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp,'admin' );

        truncate table PAP_ROLE;

    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (100, 'code1', 'role1', 'IT', 'role so 1', '{"user": 21, "role" : 15, "transaction" : 10}', 1, 'admin', systimestamp, 'admin', systimestamp,'admin');

    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (200, 'code2', 'role2', 'BUSINESS', 'role so 2', '{"user": 8, "role" : 15, "transaction" : 10}', 0, 'admin', systimestamp, 'admin', systimestamp,'admin');

  truncate table PAP_USER_ROLE;
    insert into PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (100, '100','100', 'system', systimestamp, 'system', systimestamp );
    insert into PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (200, '100','200', 'system', systimestamp, 'system', systimestamp );
    insert into PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (300, '200','100', 'system', systimestamp, 'system', systimestamp );

  """)
    @WithMockPapUser(username = "quydv2")
    @Test
    void get_Users_Not_Found() throws Exception {
        var request = MockMvcRequestBuilders.get("/users?roleCode=zzz&page=0&size=10")
            .header("Content-Type", "application/json");
        mvc.perform(request)
            .andExpect(status().is2xxSuccessful())
            .andExpect(jsonPath("$.data.content").isEmpty());
    }
    @Sql(statements = """
    truncate table  PAP_USER;

     insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (100, 'quydv2', 'idKeycloakTest','MB1234590', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 0, 'system', systimestamp, 'system', systimestamp,'admin' );

    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (200, 'tuanna', 'idKeycloakTest2','MB1234599', 'Tuan', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp,'admin' );

       truncate table PAP_ROLE;
   
    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (100, 'code1', 'role1', 'IT', 'role so 1', '{"user": 21, "role" : 15, "transaction" : 10}', 1, 'admin', systimestamp, 'admin', systimestamp,'admin');
  
    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (200, 'code2', 'role2', 'BUSINESS', 'role so 2', '{"user": 8, "role" : 15, "transaction" : 10}', 0, 'admin', systimestamp, 'admin', systimestamp,'admin');
 
  truncate table PAP_USER_ROLE;

  """)
    @WithMockPapUser(username = "quydv2")
    @Test
    void get_Users_With_No_Roles() throws Exception {
        var request = MockMvcRequestBuilders.get("/users?page=0&size=10")
                .header("Content-Type", "application/json");
        mvc.perform(request)
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.data.content.[0].id").value(100))
                .andExpect(jsonPath("$.data.content.[0].username").value("quydv2"))
                .andExpect(jsonPath("$.data.content.[0].employeeCode").value("MB1234590"))
                .andExpect(jsonPath("$.data.content.[0].fullName").value("Quy"))
                .andExpect(jsonPath("$.data.content.[0].phoneNumber").value("0987654321"))
                .andExpect(jsonPath("$.data.content.[0].email").value("test@mail.com"))
                .andExpect(jsonPath("$.data.content.[0].jobName").value("Chuyên viên"))
                .andExpect(jsonPath("$.data.content.[0].orgName").value("Khối CNTT"))
                .andExpect(jsonPath("$.data.content.[0].reason").value("admin"))
                .andExpect(jsonPath("$.data.content.[0].active").value(false))
                .andExpect(jsonPath("$.data.content.[0].createdBy").value("system"))
                .andExpect(jsonPath("$.data.content.[0].updatedBy").value("system"))
                .andExpect(jsonPath("$.data.content.[0].roles").isEmpty())

                .andExpect(jsonPath("$.data.content.[1].username").value("tuanna"))
                .andExpect(jsonPath("$.data.content.[1].employeeCode").value("MB1234599"))
                .andExpect(jsonPath("$.data.content.[1].fullName").value("Tuan"))
                .andExpect(jsonPath("$.data.content.[1].phoneNumber").value("0987654321"))
                .andExpect(jsonPath("$.data.content.[1].email").value("test@mail.com"))
                .andExpect(jsonPath("$.data.content.[1].jobName").value("Chuyên viên"))
                .andExpect(jsonPath("$.data.content.[1].orgName").value("Khối CNTT"))
                .andExpect(jsonPath("$.data.content.[1].reason").value("admin"))
                .andExpect(jsonPath("$.data.content.[1].active").value(true))
                .andExpect(jsonPath("$.data.content.[1].createdBy").value("system"))
                .andExpect(jsonPath("$.data.content.[1].updatedBy").value("system"))
                .andExpect(jsonPath("$.data.content.[0].roles").isEmpty());
    }

    @Sql(statements = """
    truncate table  PAP_USER;

     insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (100, 'quydv2', 'idKeycloakTest','MB1234590', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 0, 'system', systimestamp, 'system', systimestamp,'admin' );

    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (200, 'tuanna', 'idKeycloakTest2','MB1234599', 'Tuan', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp,'admin' );

        truncate table PAP_ROLE;
   
    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (100, 'code1', 'role1', 'IT', 'role so 1', '{"user": 21, "role" : 15, "transaction" : 10}', 1, 'admin', systimestamp, 'admin', systimestamp,'admin');
  
    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (200, 'code2', 'role2', 'BUSINESS', 'role so 2', '{"user": 8, "role" : 15, "transaction" : 10}', 0, 'admin', systimestamp, 'admin', systimestamp,'admin');
 
  truncate table PAP_USER_ROLE;
    insert into PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (100, '100','100', 'system', systimestamp, 'system', systimestamp );
    insert into PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (200, '100','200', 'system', systimestamp, 'system', systimestamp );
    insert into PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (300, '200','100', 'system', systimestamp, 'system', systimestamp );

  """)
    @WithMockPapUser(username = "quydv2")
    @Test
    void get_Users_With_WrongActiveParam() throws Exception {
        var request = MockMvcRequestBuilders.get("/users?page=0&size=10&sort=id%3AASC&active=%23$%$")
                .header("Content-Type", "application/json");
        mvc.perform(request)
                 .andExpect(status().is4xxClientError())
                 .andExpect(jsonPath("$.soaErrorCode", equalTo(Constant.PREFIX_RESPONSE_CODE + INVALID_PARAMETERS.code())))
        ;
    }

    @Test
    @WithMockPapUser(username = "quydv2")
    void create_Invalid() throws Exception {
        var requestBody = """
        {
            "username": "thombt",
            "roleIds": 1,
            "active": true
        }
        """;
        var request = MockMvcRequestBuilders.post("/users")
            .header("Content-Type", "application/json")
            .content(requestBody);
        mvc.perform(request)
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.soaErrorCode", equalTo(Constant.PREFIX_RESPONSE_CODE + ErrorCode.INVALID_PARAMETERS.code())));
    }

    @Test
    void create_Unauthorized() throws Exception {
        var requestBody = """
    {
      "username": "thombt",
      "roleIds": [1],
      "active": true
    }
    """;
        var request = MockMvcRequestBuilders.post("/users")
            .header("Content-Type", "application/json")
            .content(requestBody);
        mvc.perform(request)
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.soaErrorCode", equalTo(Constant.PREFIX_RESPONSE_CODE + ErrorCode.UNAUTHORIZED.code())));
    }

    @Sql(statements = SqlStatementTest.PERMISSIONS +"""
    truncate table PAP_USER_ROLE;
    truncate table PAP_ROLE;
    truncate table PAP_USER;

    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (1, 'quydv2', '2a562bad-6970-4703-9c66-5b887d584421','MB123456', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp );

  INSERT INTO PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, REASON, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
  VALUES (1, 'code1', 'role1', 'IT', 'role so 1', '{"user": 15, "role" : 15, "transaction" : 10}', 'abc', 1, 'admin', systimestamp, 'admin', systimestamp);
  INSERT INTO PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, REASON, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
  VALUES (2, 'code2', 'role2', 'IT', 'role so 2', '{"user": 4, "role" : 15, "transaction" : 10}', 'abc', 1, 'admin', systimestamp, 'admin', systimestamp);
  INSERT INTO PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
  VALUES (1, 1, 1, 'system', systimestamp, 'system', systimestamp);
  INSERT INTO PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
  VALUES (2, 1, 2, 'system', systimestamp, 'system', systimestamp);
  """)
    @Test
    @WithMockPapUser(username = "quydv2")
    public void testCreateUser_Success() throws Exception {
        var requestBody = """
          {
            "username": "thangnd",
            "roleIds": [1],
            "active": true,
            "reason": "reason1"
          }
        """;
        var request = MockMvcRequestBuilders.post("/users")
            .header("Content-Type", "application/json")
            .content(requestBody);
        var mvcResult = mvc.perform(request)
            .andExpect(MockMvcResultMatchers.request().asyncStarted())
            .andReturn();
        mvc.perform(MockMvcRequestBuilders.asyncDispatch(mvcResult))
            .andExpect(status().is2xxSuccessful())
            .andExpect(jsonPath("$.data.username", equalTo("thangnd")))
            .andExpect(jsonPath("$.data.orgName", equalTo("Khối Vận hành - Trung tâm Dịch vụ Vận hành số và thẻ - Phòng Vận hành & Hỗ trợ KH")))
            .andExpect(jsonPath("$.data.roles[0].roleId", equalTo(1)))
            .andExpect(jsonPath("$.data.roles[0].roleCode", equalTo("code1")))
            .andExpect(jsonPath("$.data.reason", equalTo("reason1")));
    }

    @Sql(statements = SqlStatementTest.PERMISSIONS + """
    truncate table  PAP_ROLE;
    truncate table PAP_USER;
    truncate table PAP_USER_ROLE;

    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (1, 'quydv2', '2a562bad-6970-4703-9c66-5b887d584421','MB123456', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp );

  INSERT INTO PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, REASON, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
  VALUES (1, 'code1', 'role1', 'IT', 'role so 1', '{"user": 15, "role" : 15, "transaction" : 10}', 'abc', 1, 'admin', systimestamp, 'admin', systimestamp);
  INSERT INTO PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, REASON, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
  VALUES (2, 'code2', 'role2', 'IT', 'role so 2', '{"user": 4, "role" : 15, "transaction" : 10}', 'abc', 1, 'admin', systimestamp, 'admin', systimestamp);
  INSERT INTO PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
  VALUES (1, 1, 1, 'system', systimestamp, 'system', systimestamp);
  INSERT INTO PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
  VALUES (2, 1, 2, 'system', systimestamp, 'system', systimestamp);

    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values(2, 'thangnd', 'idKeycloakTest', 'MB123456', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1,'system' , systimestamp, 'system', systimestamp)
  """)
    @Test
    @WithMockPapUser(username = "quydv2")
    void create_Duplicate() throws Exception {
        var requestBody = """
        {
            "username": "thangnd",
            "roleIds": [1],
            "active": true
        }
        """;
        var request = MockMvcRequestBuilders.post("/users")
            .header("Content-Type", "application/json")
            .content(requestBody);
        var mvcResult = mvc.perform(request)
            .andExpect(MockMvcResultMatchers.request().asyncStarted())
            .andReturn();
        mvc.perform(MockMvcRequestBuilders.asyncDispatch(mvcResult))
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.soaErrorCode", equalTo(Constant.PREFIX_RESPONSE_CODE + ErrorCode.DUPLICATE_USER.code())));
    }

    @Sql(statements = SqlStatementTest.PERMISSIONS + """
    truncate table PAP_USER_ROLE;
    truncate table PAP_ROLE;
    truncate table PAP_USER;
  
    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (1, 'quydv2', '2a562bad-6970-4703-9c66-5b887d584421','MB123456', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp );
  
    INSERT INTO PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, REASON, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES (1, 'code1', 'role1', 'IT', 'role so 1', '{"user": 15, "role" : 15, "transaction" : 10}', 'abc', 1, 'admin', systimestamp, 'admin', systimestamp);
    INSERT INTO PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, REASON, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES (2, 'code2', 'role2', 'IT', 'role so 2', '{"user": 4, "role" : 15, "transaction" : 10}', 'abc', 1, 'admin', systimestamp, 'admin', systimestamp);
    INSERT INTO PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES (1, 1, 1, 'system', systimestamp, 'system', systimestamp);
    INSERT INTO PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES (2, 1, 2, 'system', systimestamp, 'system', systimestamp);
    """)
    @Test
    @WithMockPapUser(username = "quydv2")
    public void testCreateUser_ErrorCreateUserRoleRollbackCreateUser() throws Exception {
        var requestBody = """
        {
          "username": "thangnd",
          "roleIds": [1],
          "active": true,
          "reason": "reason1"
        }
        """;
        var request = MockMvcRequestBuilders.post("/users")
            .header("Content-Type", "application/json")
            .content(requestBody);
        var mvcResult = mvc.perform(request)
            .andExpect(MockMvcResultMatchers.request().asyncStarted())
            .andReturn();

        Mockito.doThrow(new RuntimeException("Failed to create user roles"))
            .when(userRoleService)
            .createUserRoles(Mockito.anyString(), Mockito.anyLong(), Mockito.anyList());

        mvc.perform(MockMvcRequestBuilders.asyncDispatch(mvcResult))
            .andExpect(status().is5xxServerError())
            .andExpect(jsonPath("$.soaErrorDesc", equalTo("Failed to create user roles")));

        Optional<User> createdUser = userRepository.findByUsername("hcm-full-user");
        assertThat(createdUser).isEmpty();
    }

  @Sql(statements = SqlStatementTest.PERMISSIONS + """
    truncate table PAP_USER_ROLE;
    truncate table PAP_ROLE;
    truncate table PAP_USER;
  
    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (1, 'quydv2', '2a562bad-6970-4703-9c66-5b887d584421','MB123456', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp );
  
    INSERT INTO PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, REASON, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES (1, 'code1', 'role1', 'IT', 'role so 1', '{"user": 15, "role" : 15, "transaction" : 10}', 'abc', 1, 'admin', systimestamp, 'admin', systimestamp);
    INSERT INTO PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, REASON, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES (2, 'code2', 'role2', 'IT', 'role so 2', '{"user": 4, "role" : 15, "transaction" : 10}', 'abc', 1, 'admin', systimestamp, 'admin', systimestamp);
    INSERT INTO PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES (1, 1, 1, 'system', systimestamp, 'system', systimestamp);
    INSERT INTO PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES (2, 1, 2, 'system', systimestamp, 'system', systimestamp);
    """)
  @Test
  @WithMockPapUser(username = "quydv2")
  public void testCreateUser_ErrorAssignRoleRollbackCreateUserAndUserRole() throws Exception {
    var requestBody = """
        {
          "username": "thangnd",
          "roleIds": [1],
          "active": true,
          "reason": "reason1"
        }
        """;
      var request = MockMvcRequestBuilders.post("/users")
          .header("Content-Type", "application/json")
          .content(requestBody);
      var mvcResult = mvc.perform(request)
          .andExpect(MockMvcResultMatchers.request().asyncStarted())
          .andReturn();

      Mockito.doThrow(new RuntimeException("Failed to assign role to user"))
          .when(keycloakInternalClient)
          .assignRoleToUser(Mockito.any(), Mockito.anyString());

    mvc.perform(MockMvcRequestBuilders.asyncDispatch(mvcResult))
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.soaErrorDesc", equalTo("Failed to assign role to user")));

      Optional<User> createdUser = userRepository.findByUsername("hcm-full-user");
      assertThat(createdUser).isEmpty();

      List<RoleInfo> userRole = userRoleService.findRolesByUserId(2L);
      assertThat(userRole).isEmpty();
  }
    @Sql(statements = """
    truncate table  PAP_USER;

     insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (100, 'quydv2', 'idKeycloakTest','MB1234590', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 0, 'system', systimestamp, 'system', systimestamp,'admin' );

    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (200, 'tuanna', 'idKeycloakTest2','MB1234599', 'Tuan', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp,'admin' );

        truncate table PAP_ROLE;

    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (100, 'code1', 'role1', 'IT', 'role so 1', '{"user": 21, "role" : 15, "transaction" : 10}', 1, 'admin', systimestamp, 'admin', systimestamp,'admin');

    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (200, 'code2', 'role2', 'BUSINESS', 'role so 2', '{"user": 8, "role" : 15, "transaction" : 10}', 0, 'admin', systimestamp, 'admin', systimestamp,'admin');

  truncate table PAP_USER_ROLE;
    insert into PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (100, '100','100', 'system', systimestamp, 'system', systimestamp );
    insert into PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (200, '100','200', 'system', systimestamp, 'system', systimestamp );
    insert into PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (300, '200','100', 'system', systimestamp, 'system', systimestamp );

  """)
    @WithMockPapUser(username = "quydv2")
    @Test
    void getUserDetail_Success() throws Exception {
        var request = MockMvcRequestBuilders.get("/users/100/detail")
            .header("Content-Type", "application/json");
        mvc.perform(request)
            .andExpect(jsonPath("$.data").isNotEmpty())
            .andExpect(jsonPath("$.data.id").value(100))
            .andExpect(jsonPath("$.data.username").value("quydv2"))
            .andExpect(jsonPath("$.data.employeeCode").value("MB1234590"))
            .andExpect(jsonPath("$.data.fullName").value("Quy"))
            .andExpect(jsonPath("$.data.phoneNumber").value("0987654321"))
            .andExpect(jsonPath("$.data.email").value("test@mail.com"))
            .andExpect(jsonPath("$.data.jobName").value("Chuyên viên"))
            .andExpect(jsonPath("$.data..orgName").value("Khối CNTT"))
            .andExpect(jsonPath("$.data.reason").value("admin"))
            .andExpect(jsonPath("$.data.active").value(false))
            .andExpect(jsonPath("$.data.createdBy").value("system"))
            .andExpect(jsonPath("$.data.updatedBy").value("system"))
            .andExpect(jsonPath("$.data.roles.[0].roleId").value(100))
            .andExpect(jsonPath("$.data.roles.[0].roleCode").value("code1"))
            .andExpect(jsonPath("$.data.roles.[0].roleName").value("role1"))
        .andExpect(jsonPath("$.data.roles.[0].roleType").value("IT"));
    }
    @Sql(statements = """
    truncate table  PAP_USER;

     insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (100, 'quydv2', 'idKeycloakTest','MB1234590', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 0, 'system', systimestamp, 'system', systimestamp,'admin' );

    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (200, 'tuanna', 'idKeycloakTest2','MB1234599', 'Tuan', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp,'admin' );

        truncate table PAP_ROLE;

    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (100, 'code1', 'role1', 'IT', 'role so 1', '{"user": 21, "role" : 15, "transaction" : 10}', 1, 'admin', systimestamp, 'admin', systimestamp,'admin');

    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (200, 'code2', 'role2', 'BUSINESS', 'role so 2', '{"user": 8, "role" : 15, "transaction" : 10}', 0, 'admin', systimestamp, 'admin', systimestamp,'admin');

  truncate table PAP_USER_ROLE;
    insert into PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (100, '100','100', 'system', systimestamp, 'system', systimestamp );
    insert into PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (200, '100','200', 'system', systimestamp, 'system', systimestamp );
    insert into PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (300, '200','100', 'system', systimestamp, 'system', systimestamp );

  """)
    @WithMockPapUser(username = "quydv2")
    @Test
    void getUserDetail_NotFound() throws Exception {
        var request = MockMvcRequestBuilders.get("/users/1/detail")
            .header("Content-Type", "application/json");
        mvc.perform(request)
            .andExpect(status().is4xxClientError());
    }
    @Sql(statements = """
    truncate table PAP_USER;
    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME,ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (1, 'quydv2', 'idKeycloakTest','MB123456', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT' ,1, 'system', systimestamp, 'system', systimestamp,'admin' );

    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (2, 'tuanna', 'idKeycloakTest2','MB1234567', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp,'admin' );
  """)
    @WithMockPapUser(username = "quydv2")
    @Test
    void update_Deactive_User_But_Revoke_Role_Failed() throws Exception {
        var requestBody = """
        {
            "reason": "CNTT",
            "active": false
        }
        """;
        var request = MockMvcRequestBuilders.put("/users/2")
            .header("Content-Type", "application/json")
            .content(requestBody);


        Mockito.doThrow(new RuntimeException("Failed to revoke role to user"))
            .when(keycloakInternalClient)
            .revokeRoleFromUser(Mockito.any(), Mockito.anyString());
        mvc.perform(request)
            .andExpect(status().is5xxServerError())
            .andExpect(jsonPath("$.soaErrorDesc", equalTo("Can not revoke role from user id: 2")));
        Optional<User> user = userRepository.findById(2L);
        assertThat(user.get().isActive()).isEqualTo(true);
        assertThat(user.get().getReason()).isEqualTo("admin");
    }
    @Sql(statements = """
    truncate table PAP_USER;
    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME,ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (1, 'quydv2', 'idKeycloakTest','MB123456', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT' ,1, 'system', systimestamp, 'system', systimestamp,'admin' );

    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (2, 'tuanna', 'idKeycloakTest2','MB1234567', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 0, 'system', systimestamp, 'system', systimestamp,'admin' );
  """)
    @WithMockPapUser(username = "quydv2")
    @Test

    void update_Active_User_But_Assign_Role_Failed() throws Exception {
        var requestBody = """
        {
            "reason": "CNTT",
            "active": true
        }
        """;
        var request = MockMvcRequestBuilders.put("/users/2")
            .header("Content-Type", "application/json")
            .content(requestBody);


        Mockito.doThrow(new RuntimeException("Failed to assign role to user"))
            .when(keycloakInternalClient)
            .revokeRoleFromUser(Mockito.any(), Mockito.anyString());

        mvc.perform(request)
            .andExpect(status().is5xxServerError())
            .andExpect(jsonPath("$.soaErrorDesc", equalTo("Can not assign role from user id: 2")));
        Optional<User> user = userRepository.findById(2L);
        assertThat(user.get().isActive()).isEqualTo(false);
        assertThat(user.get().getReason()).isEqualTo("admin");
    }
    @Sql(statements = SqlStatementTest.PERMISSIONS + """
    truncate table PAP_USER;
    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME,ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (1, 'quydv2', 'idKeycloakTest','MB123456', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT' ,1, 'system', systimestamp, 'system', systimestamp,'admin' );

    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (2, 'tuanna', 'idKeycloakTest2','MB1234567', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 0, 'system', systimestamp, 'system', systimestamp,'admin' );
    truncate table PAP_ROLE;
 INSERT INTO PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, REASON, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES (1, 'code1', 'role1', 'IT', 'role so 1', '{"user": 15, "role" : 15, "transaction" : 10}', 'abc', 1, 'admin', systimestamp, 'admin', systimestamp);
    INSERT INTO PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, REASON, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES (2, 'code2', 'role2', 'IT', 'role so 2', '{"user": 4, "role" : 15, "transaction" : 10}', 'abc', 1, 'admin', systimestamp, 'admin', systimestamp);
  """)
    @WithMockPapUser(username = "quydv2")
    @Test

    void update_Active_User_But_Active_Null() throws Exception {
        var requestBody = """
        {
            "reason": "CNTT",
            "roleIds": [1,2]
        }
        """;
        var request = MockMvcRequestBuilders.put("/users/2")
            .header("Content-Type", "application/json")
            .content(requestBody);

        mvc.perform(request)
            .andExpect(status().is2xxSuccessful());
        Optional<User> user = userRepository.findById(2L);
        assertThat(user.get().isActive()).isEqualTo(false);
    }
    @Sql(statements = SqlStatementTest.PERMISSIONS + """
    truncate table PAP_USER_ROLE;
    truncate table PAP_ROLE;
    truncate table PAP_USER;
    truncate table PAP_USER_ROLE_HISTORY;
  
    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (1, 'quydv2', '2a562bad-6970-4703-9c66-5b887d584421','MB123456', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp );
  
    INSERT INTO PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, REASON, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES (1, 'code1', 'role1', 'IT', 'role so 1', '{"user": 15, "role" : 15, "transaction" : 10}', 'abc', 1, 'admin', systimestamp, 'admin', systimestamp);
    INSERT INTO PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, REASON, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES (2, 'code2', 'role2', 'IT', 'role so 2', '{"user": 4, "role" : 15, "transaction" : 10}', 'abc', 1, 'admin', systimestamp, 'admin', systimestamp);
    INSERT INTO PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES (1, 1, 1, 'system', systimestamp, 'system', systimestamp);
    INSERT INTO PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES (2, 1, 2, 'system', systimestamp, 'system', systimestamp);
    """)
    @Test
    @WithMockPapUser(username = "quydv2")
    public void update_Remove_Role() throws Exception {
        var requestBody = """
        {
          "roleIds": [1],
          "reason": "reason1"
        }
        """;
        var request = MockMvcRequestBuilders.put("/users/1")
            .header("Content-Type", "application/json")
            .content(requestBody);


        mvc.perform(request).andExpect(status().is2xxSuccessful());

        Optional<User> updatedUser = userRepository.findByUsername("quydv2");
        assertThat(updatedUser).isNotEmpty();
        assertThat(updatedUser.get().getReason()).isEqualTo("reason1");
        assertThat(updatedUser.get().isActive()).isEqualTo(true);

        List<RoleInfo> userRole = userRoleService.findRolesByUserId(1L);
        assertThat(userRole.get(0).getRoleId()).isEqualTo(1L);
        assertThat(userRole.size()).isEqualTo(1);

        userRoleHistoryRepository.findOne(Example.of(new UserRoleHistory().setAction(Action.DELETE).setRoleId(2L)
                .setUserId(1L)))
            .ifPresentOrElse(userRoleHistory -> {
                assertEquals(Action.DELETE, userRoleHistory.getAction());
                assertEquals(1L, userRoleHistory.getUserId());
                assertEquals(2L, userRoleHistory.getRoleId());
                assertEquals("quydv2", userRoleHistory.getCreatedBy());
                assertNotNull(userRoleHistory.getCreatedAt());
                assertEquals("quydv2", userRoleHistory.getUpdatedBy());
                assertNotNull(userRoleHistory.getUpdatedAt());
            }, Assertions::fail);
    }
    @Sql(statements = SqlStatementTest.PERMISSIONS + """
    truncate table PAP_USER_ROLE;
    truncate table PAP_ROLE;
    truncate table PAP_USER;
  
    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (1, 'quydv2', '2a562bad-6970-4703-9c66-5b887d584421','MB123456', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp );
  insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (2, 'tuanna', 'idKeycloakTest2','MB1234567', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp,'admin' );

    INSERT INTO PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, REASON, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES (1, 'code1', 'role1', 'IT', 'role so 1', '{"user": 15, "role" : 15, "transaction" : 10}', 'abc', 1, 'admin', systimestamp, 'admin', systimestamp);
    INSERT INTO PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, REASON, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES (2, 'code2', 'role2', 'IT', 'role so 2', '{"user": 4, "role" : 15, "transaction" : 10}', 'abc', 1, 'admin', systimestamp, 'admin', systimestamp);
    INSERT INTO PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES (1, 1, 1, 'system', systimestamp, 'system', systimestamp);
    INSERT INTO PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES (2, 1, 2, 'system', systimestamp, 'system', systimestamp);
    """)
    @Test
    @WithMockPapUser(username = "quydv2")
    public void update_Add_Role() throws Exception {
        var requestBody = """
        {
          "roleIds": [1,2],
          "reason": "reason1"
        }
        """;
        var request = MockMvcRequestBuilders.put("/users/2")
            .header("Content-Type", "application/json")
            .content(requestBody);


        mvc.perform(request).andExpect(status().is2xxSuccessful());

        Optional<User> updatedUser = userRepository.findByUsername("tuanna");
        assertThat(updatedUser).isNotEmpty();
        assertThat(updatedUser.get().getReason()).isEqualTo("reason1");
        assertThat(updatedUser.get().isActive()).isEqualTo(true);

        List<RoleInfo> userRole = userRoleService.findRolesByUserId(1L);
        assertThat(userRole.get(0).getRoleId()).isEqualTo(1L);
        assertThat(userRole.get(1).getRoleId()).isEqualTo(2L);
        assertThat(userRole.size()).isEqualTo(2);
    }
    @Sql(statements = SqlStatementTest.PERMISSIONS + """
    truncate table PAP_USER_ROLE;
    truncate table PAP_ROLE;
    truncate table PAP_USER;
  
    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
      values(2, 'thangnd', 'cf1bfc7e-9126-4884-8cb0-ce645c821a45', 'MB123456', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 0,'system' , systimestamp, 'system', systimestamp,'admin');

    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (1, 'tuanna', 'idKeycloakTest2','MB1234567', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp,'admin' );

    INSERT INTO PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, REASON, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES (1, 'code1', 'role1', 'IT', 'role so 1', '{"user": 15, "role" : 15, "transaction" : 10}', 'abc', 1, 'admin', systimestamp, 'admin', systimestamp);
    INSERT INTO PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, REASON, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES (2, 'code2', 'role2', 'IT', 'role so 2', '{"user": 4, "role" : 15, "transaction" : 10}', 'abc', 1, 'admin', systimestamp, 'admin', systimestamp);
    INSERT INTO PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES (1, 1, 1, 'system', systimestamp, 'system', systimestamp);
    INSERT INTO PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES (2, 1, 2, 'system', systimestamp, 'system', systimestamp);
    """)
    @Test
    @WithMockPapUser(username = "tuanna")
    public void update_Add_Role_And_Active_User() throws Exception {
        var requestBody = """
        {
          "roleIds": [1,2],
          "active": true,
          "reason": "reason1"
        }
        """;
        var request = MockMvcRequestBuilders.put("/users/2")
            .header("Content-Type", "application/json")
            .content(requestBody);

        mvc.perform(request)
            .andExpect(status().is2xxSuccessful())
            .andExpect(jsonPath("$.data.reason").value("reason1"))
            .andExpect(jsonPath("$.data.active").value(true));
        Optional<User> updatedUser = userRepository.findByUsername("thangnd");
        assertThat(updatedUser).isNotEmpty();
        assertThat(updatedUser.get().getReason()).isEqualTo("reason1");
        assertThat(updatedUser.get().isActive()).isEqualTo(true);

        List<RoleInfo> userRole = userRoleService.findRolesByUserId(2L);
        assertThat(userRole.get(0).getRoleId()).isEqualTo(1L);
        assertThat(userRole.get(1).getRoleId()).isEqualTo(2L);
        assertThat(userRole.size()).isEqualTo(2);
    }
    @Sql(statements = SqlStatementTest.PERMISSIONS + """
    truncate table PAP_USER_ROLE;
    truncate table PAP_ROLE;
    truncate table PAP_USER;
    truncate table PAP_USER_ROLE_HISTORY;
  
    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
      values(2, 'quydv2', 'idKeycloakTest1', 'MB123456', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1,'system' , systimestamp, 'system', systimestamp,'admin');

    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (1, 'tuanna', 'idKeycloakTest2','MB1234567', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp,'admin' );

    INSERT INTO PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, REASON, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES (1, 'code1', 'role1', 'IT', 'role so 1', '{"user": 15, "role" : 15, "transaction" : 10}', 'abc', 1, 'admin', systimestamp, 'admin', systimestamp);
    INSERT INTO PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, REASON, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES (2, 'code2', 'role2', 'IT', 'role so 2', '{"user": 4, "role" : 15, "transaction" : 10}', 'abc', 1, 'admin', systimestamp, 'admin', systimestamp);
    INSERT INTO PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES (1, 1, 1, 'system', systimestamp, 'system', systimestamp);
    INSERT INTO PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES (2, 1, 2, 'system', systimestamp, 'system', systimestamp);
    INSERT INTO PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES (3, 2, 1, 'system', systimestamp, 'system', systimestamp);
    INSERT INTO PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES (4, 2, 2, 'system', systimestamp, 'system', systimestamp);
    """)
    @Test
    @WithMockPapUser(username = "tuanna")
    public void update_RemoveAllRoles() throws Exception {
        var requestBody = """
        {
          "roleIds": [],
          "reason": "reason1"
        }
        """;
        var request = MockMvcRequestBuilders.put("/users/2")
            .header("Content-Type", "application/json")
            .content(requestBody);
        List<RoleInfo> userRole = userRoleService.findRolesByUserId(2L);
        assertThat(userRole.size()).isEqualTo(2);
        mvc.perform(request)
            .andExpect(status().is2xxSuccessful())
            .andExpect(jsonPath("$.data.reason").value("reason1"))
            .andExpect(jsonPath("$.data.active").value(true));
        Optional<User> updatedUser = userRepository.findByUsername("quydv2");
        assertThat(updatedUser).isNotEmpty();
        assertThat(updatedUser.get().getReason()).isEqualTo("reason1");
        assertThat(updatedUser.get().isActive()).isEqualTo(true);

        userRole = userRoleService.findRolesByUserId(2L);
        assertThat(userRole.size()).isEqualTo(0);
        userRoleHistoryRepository.findOne(Example.of(new UserRoleHistory().setAction(Action.DELETE).setRoleId(1L)
                .setUserId(2L)))
            .ifPresentOrElse(userRoleHistory -> {
                assertEquals(Action.DELETE, userRoleHistory.getAction());
                assertEquals(2L, userRoleHistory.getUserId());
                assertEquals(1L, userRoleHistory.getRoleId());
                assertEquals("tuanna", userRoleHistory.getCreatedBy());
                assertNotNull(userRoleHistory.getCreatedAt());
                assertEquals("tuanna", userRoleHistory.getUpdatedBy());
                assertNotNull(userRoleHistory.getUpdatedAt());
            }, Assertions::fail);

        userRoleHistoryRepository.findOne(Example.of(new UserRoleHistory().setAction(Action.DELETE).setRoleId(2L)
                .setUserId(2L)))
            .ifPresentOrElse(userRoleHistory -> {
                assertEquals(Action.DELETE, userRoleHistory.getAction());
                assertEquals(2L, userRoleHistory.getUserId());
                assertEquals(2L, userRoleHistory.getRoleId());
                assertEquals("tuanna", userRoleHistory.getCreatedBy());
                assertNotNull(userRoleHistory.getCreatedAt());
                assertEquals("tuanna", userRoleHistory.getUpdatedBy());
                assertNotNull(userRoleHistory.getUpdatedAt());
            }, Assertions::fail);
    }
    @Sql(statements = SqlStatementTest.PERMISSIONS + """
    truncate table PAP_USER_ROLE;
    truncate table PAP_ROLE;
    truncate table PAP_USER;
    truncate table PAP_USER_ROLE_HISTORY;
  
    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
      values(2, 'quydv2', 'idKeycloakTest1', 'MB123456', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1,'system' , systimestamp, 'system', systimestamp,'admin');

    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (1, 'tuanna', 'idKeycloakTest2','MB1234567', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp,'admin' );

    INSERT INTO PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, REASON, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES (1, 'code1', 'role1', 'IT', 'role so 1', '{"user": 15, "role" : 15, "transaction" : 10}', 'abc', 1, 'admin', systimestamp, 'admin', systimestamp);
    INSERT INTO PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, REASON, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES (2, 'code2', 'role2', 'IT', 'role so 2', '{"user": 4, "role" : 15, "transaction" : 10}', 'abc', 1, 'admin', systimestamp, 'admin', systimestamp);
    INSERT INTO PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES (1, 1, 1, 'system', systimestamp, 'system', systimestamp);
    INSERT INTO PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES (2, 1, 2, 'system', systimestamp, 'system', systimestamp);
    INSERT INTO PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES (3, 2, 1, 'system', systimestamp, 'system', systimestamp);
    """)
    @Test
    @WithMockPapUser(username = "tuanna")
    public void update_ChangeRoles() throws Exception {
        var requestBody = """
        {
          "roleIds": [2],
          "reason": "reason1"
        }
        """;
        var request = MockMvcRequestBuilders.put("/users/2")
            .header("Content-Type", "application/json")
            .content(requestBody);
        List<RoleInfo> userRole = userRoleService.findRolesByUserId(2L);
        assertThat(userRole.size()).isEqualTo(1);
        mvc.perform(request)
            .andExpect(status().is2xxSuccessful())
            .andExpect(jsonPath("$.data.reason").value("reason1"))
            .andExpect(jsonPath("$.data.active").value(true));
        Optional<User> updatedUser = userRepository.findByUsername("quydv2");
        assertThat(updatedUser).isNotEmpty();
        assertThat(updatedUser.get().getReason()).isEqualTo("reason1");
        assertThat(updatedUser.get().isActive()).isEqualTo(true);

        userRole = userRoleService.findRolesByUserId(2L);
        assertThat(userRole.size()).isEqualTo(1);
        assertThat(userRole.get(0).getRoleId()).isEqualTo(2L);
        userRoleHistoryRepository.findOne(Example.of(new UserRoleHistory().setAction(Action.DELETE).setRoleId(1L)
                .setUserId(2L)))
            .ifPresentOrElse(userRoleHistory -> {
                assertEquals(Action.DELETE, userRoleHistory.getAction());
                assertEquals(2L, userRoleHistory.getUserId());
                assertEquals(1L, userRoleHistory.getRoleId());
                assertEquals("tuanna", userRoleHistory.getCreatedBy());
                assertNotNull(userRoleHistory.getCreatedAt());
                assertEquals("tuanna", userRoleHistory.getUpdatedBy());
                assertNotNull(userRoleHistory.getUpdatedAt());
            }, Assertions::fail);
        assertThat(userRole.get(0).getRoleId()).isEqualTo(2L);
        userRoleHistoryRepository.findOne(Example.of(new UserRoleHistory().setAction(Action.INSERT).setRoleId(2L)
                .setUserId(2L)))
            .ifPresentOrElse(userRoleHistory -> {
                assertEquals(Action.INSERT, userRoleHistory.getAction());
                assertEquals(2L, userRoleHistory.getUserId());
                assertEquals(2L, userRoleHistory.getRoleId());
                assertEquals("tuanna", userRoleHistory.getCreatedBy());
                assertNotNull(userRoleHistory.getCreatedAt());
                assertEquals("tuanna", userRoleHistory.getUpdatedBy());
                assertNotNull(userRoleHistory.getUpdatedAt());
            }, Assertions::fail);
    }

    @Sql(statements = SqlStatementTest.PERMISSIONS + """
        truncate table  PAP_ROLE;
        truncate table PAP_USER;
        truncate table PAP_USER_ROLE;
              
        insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
        values (1, 'quydv2', 'idKeycloakTest','MB123456', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp );
              
        insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
        values (1, 'code1', 'role1', 'IT', 'role so 1', '{"user": 21, "role" : 15, "permission" : 1}', 1, 'admin', systimestamp, 'admin', systimestamp);
          
        insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
        values (2, 'code2', 'role2', 'BUSINESS', 'role so 2', '{"user": 8, "role" : 15, "routing-whitelist" : 10}', 0, 'admin', systimestamp, 'admin', systimestamp);
         
        insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
        values (3, 'code3', 'role3', 'ADMIN', 'role so 3', '{"user": 8, "role" : 15, "routing-whitelist" : 10}', 1, 'admin', systimestamp, 'admin', systimestamp);
            
        INSERT INTO PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
        values (1, 1, 1, 'system', systimestamp, 'system', systimestamp);
        """)
    @WithMockPapUser(username = "quydv2")
    @Test
    void getPermissions_Success() throws Exception {
        var request = MockMvcRequestBuilders.get("/users/permissions")
            .header("Content-Type", "application/json");
        mvc.perform(request)
            .andExpect(status().is2xxSuccessful())
            .andExpect(jsonPath("$.data").isNotEmpty())
        ;
    }

    @Sql(statements = SqlStatementTest.PERMISSIONS + """
        truncate table  PAP_ROLE;
        truncate table PAP_USER;
        truncate table PAP_USER_ROLE;
              
        insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
        values (1, 'quydv2', 'idKeycloakTest','MB123456', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp );
              
        insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
        values (1, 'code1', 'role1', 'IT', 'role so 1', '{"user": 21, "role" : 15, "permission" : 1}', 1, 'admin', systimestamp, 'admin', systimestamp);
          
        insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
        values (2, 'code2', 'role2', 'BUSINESS', 'role so 2', '{"user": 8, "role" : 15, "routing-whitelist" : 10}', 0, 'admin', systimestamp, 'admin', systimestamp);
         
        insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
        values (3, 'code3', 'role3', 'ADMIN', 'role so 3', '{"user": 8, "role" : 15, "routing-whitelist" : 10}', 1, 'admin', systimestamp, 'admin', systimestamp);
            
        INSERT INTO PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
        values (1, 1, 1, 'system', systimestamp, 'system', systimestamp);
        """)
    @WithMockPapUser(username = "quydv2")
    @Test
    void getPermission_Fail() throws Exception {
        var request = MockMvcRequestBuilders.get("/users/permissions")
            .header("Content-Type", "application/json");
        when(userService.getPermissions()).thenThrow(new RuntimeException());
        mvc.perform(request)
            .andExpect(status().is5xxServerError())
            .andExpect(jsonPath("$.soaErrorCode").value(Constant.PREFIX_RESPONSE_CODE + ErrorCode.INTERNAL_SERVER_ERROR.code()))
        ;
    }

    @Sql(statements = SqlStatementTest.PERMISSIONS + """
        truncate table  PAP_ROLE;
        truncate table PAP_USER;
        truncate table PAP_USER_ROLE;
        insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
        values (1, 'quydv2', 'idKeycloakTest','MB123456', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp );
              
        insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
        values (1, 'code1', 'role1', 'IT', 'role so 1', '{"user": 21, "role" : 15, "permission" : 1}', 1, 'admin', systimestamp, 'admin', systimestamp);
          
        insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
        values (2, 'code2', 'role2', 'BUSINESS', 'role so 2', '{"user": 8, "role" : 15, "routing-whitelist" : 10}', 0, 'admin', systimestamp, 'admin', systimestamp);
         
        insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
        values (3, 'code3', 'role3', 'ADMIN', 'role so 3', '{"user": 8, "role" : 15, "routing-whitelist" : 10}', 1, 'admin', systimestamp, 'admin', systimestamp);
            
        INSERT INTO PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
        values (1, 1, 1, 'system', systimestamp, 'system', systimestamp);
        """)
    @WithMockPapUser(username = "quydv2")
    @Test
    void getHCMUser_Success() throws Exception {
        var request = MockMvcRequestBuilders.get("/users/hcm/thangnd")
            .header("Content-Type", "application/json");
        when(userService.getPermissions()).thenThrow(new RuntimeException());
        var mvcResult = mvc.perform(request)
            .andExpect(MockMvcResultMatchers.request().asyncStarted())
            .andReturn();
        mvc.perform(MockMvcRequestBuilders.asyncDispatch(mvcResult))
            .andExpect(status().is2xxSuccessful())
            .andExpect(jsonPath("$.data.fullName", equalTo("Nguyễn Duy Thắng")))
         .andExpect(jsonPath("$.data.mobileNumber", equalTo("0983321541")))
            .andExpect(jsonPath("$.data.email", equalTo("thangnd@dev.mbbank.com.vn")));
    }

    @Sql(statements = """
    truncate table  PAP_USER;

     insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (100, 'quydv2', 'idKeycloakTest','MB1234590', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp,'admin' );

    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (200, 'tuanna', 'idKeycloakTest2','MB1234599', 'Tuan', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp,'admin' );

        truncate table PAP_ROLE;

    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (100, 'code1', 'role1', 'IT', 'role so 1', '{"user": 21, "role" : 15, "transaction" : 10}', 1, 'admin', systimestamp, 'admin', systimestamp,'admin');

    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (200, 'code2', 'role2', 'BUSINESS', 'role so 2', '{"user": 8, "role" : 15, "transaction" : 10}', 0, 'admin', systimestamp, 'admin', systimestamp,'admin');

  truncate table PAP_USER_ROLE;
    insert into PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (100, '100','100', 'system', systimestamp, 'system', systimestamp );
    insert into PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (200, '100','200', 'system', systimestamp, 'system', systimestamp );
    insert into PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (300, '200','100', 'system', systimestamp, 'system', systimestamp );

  """)
    @WithMockPapUser(username = "quydv2")
    @Test
    void getPersonalInfo_Success() throws Exception {
        var request = MockMvcRequestBuilders.get("/users/me")
            .header("Content-Type", "application/json");
        mvc.perform(request)
            .andExpect(jsonPath("$.data").isNotEmpty())
            .andExpect(jsonPath("$.data.id").value(100))
            .andExpect(jsonPath("$.data.username").value("quydv2"))
            .andExpect(jsonPath("$.data.employeeCode").value("MB1234590"))
            .andExpect(jsonPath("$.data.fullName").value("Quy"))
            .andExpect(jsonPath("$.data.phoneNumber").value("0987654321"))
            .andExpect(jsonPath("$.data.email").value("test@mail.com"))
            .andExpect(jsonPath("$.data.jobName").value("Chuyên viên"))
            .andExpect(jsonPath("$.data..orgName").value("Khối CNTT"))
            .andExpect(jsonPath("$.data.reason").value("admin"))
            .andExpect(jsonPath("$.data.active").value(true))
            .andExpect(jsonPath("$.data.createdBy").value("system"))
            .andExpect(jsonPath("$.data.updatedBy").value("system"))
            .andExpect(jsonPath("$.data.roles.[0].roleId").value(100))
            .andExpect(jsonPath("$.data.roles.[0].roleCode").value("code1"))
            .andExpect(jsonPath("$.data.roles.[0].roleName").value("role1"))
            .andExpect(jsonPath("$.data.roles.[0].roleType").value("IT"));
    }

    @Sql(statements = """
    truncate table  PAP_USER;

    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (200, 'tuanna', 'idKeycloakTest2','MB1234599', 'Tuan', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp,'admin' );

    truncate table PAP_ROLE;

    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (100, 'code1', 'role1', 'IT', 'role so 1', '{"user": 21, "role" : 15, "transaction" : 10}', 1, 'admin', systimestamp, 'admin', systimestamp,'admin');

    insert into PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (200, 'code2', 'role2', 'BUSINESS', 'role so 2', '{"user": 8, "role" : 15, "transaction" : 10}', 0, 'admin', systimestamp, 'admin', systimestamp,'admin');

    truncate table PAP_USER_ROLE;
 
    insert into PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (300, '200','100', 'system', systimestamp, 'system', systimestamp );

  """)
    @WithMockPapUser(username = "quydv2")
    @Test
    void getPersonalInfo_Fail() throws Exception {
        var request = MockMvcRequestBuilders.get("/users/me")
            .header("Content-Type", "application/json");
        mvc.perform(request)
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.soaErrorCode").value(Constant.PREFIX_RESPONSE_CODE + ErrorCode.USER_NOT_FOUND.code()))
           ;
    }

    @Sql(statements = SqlStatementTest.PERMISSIONS + """
    truncate table PAP_USER_ROLE;
    truncate table PAP_ROLE;
    truncate table PAP_USER;
  
    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (1, 'quydv2', '2a562bad-6970-4703-9c66-5b887d584421','MB123456', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp );
  insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,REASON)
    values (2, 'tuanna', 'idKeycloakTest2','MB1234567', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp,'admin' );

    INSERT INTO PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, REASON, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES (1, 'code1', 'role1', 'IT', 'role so 1', '{"user": 15, "role" : 15, "transaction" : 10}', 'abc', 0, 'admin', systimestamp, 'admin', systimestamp);
    INSERT INTO PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, REASON, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES (2, 'code2', 'role2', 'IT', 'role so 2', '{"user": 4, "role" : 15, "transaction" : 10}', 'abc', 1, 'admin', systimestamp, 'admin', systimestamp);
    INSERT INTO PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES (1, 1, 1, 'system', systimestamp, 'system', systimestamp);
    INSERT INTO PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    VALUES (2, 1, 2, 'system', systimestamp, 'system', systimestamp);
    """)
    @Test
    @WithMockPapUser(username = "quydv2")
    public void update_failed_role_deactivated() throws Exception {
        var requestBody = """
        {
          "roleIds": [1,2],
          "reason": "reason1"
        }
        """;
        var request = MockMvcRequestBuilders.put("/users/2")
            .header("Content-Type", "application/json")
            .content(requestBody);
        mvc.perform(request).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.soaErrorCode").value(Constant.PREFIX_RESPONSE_CODE + ROLE_ID_NOT_EXIST.code()));
    }

    @Sql(statements = SqlStatementTest.PERMISSIONS +"""
    truncate table PAP_USER_ROLE;
    truncate table PAP_ROLE;
    truncate table PAP_USER;

    insert into PAP_USER (ID, USERNAME, KEYCLOAK_ID, EMPLOYEE_CODE, FULL_NAME, PHONE_NUMBER, EMAIL,JOB_NAME, ORG_NAME, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
    values (1, 'quydv2', '2a562bad-6970-4703-9c66-5b887d584421','MB123456', 'Quy', '0987654321', 'test@mail.com', 'Chuyên viên', 'Khối CNTT', 1, 'system', systimestamp, 'system', systimestamp );

  INSERT INTO PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, REASON, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
  VALUES (1, 'code1', 'role1', 'IT', 'role so 1', '{"user": 15, "role" : 15, "transaction" : 10}', 'abc', 1, 'admin', systimestamp, 'admin', systimestamp);
  INSERT INTO PAP_ROLE (ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, REASON, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
  VALUES (2, 'code2', 'role2', 'IT', 'role so 2', '{"user": 4, "role" : 15, "transaction" : 10}', 'abc', 0, 'admin', systimestamp, 'admin', systimestamp);
  INSERT INTO PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
  VALUES (1, 1, 1, 'system', systimestamp, 'system', systimestamp);
  INSERT INTO PAP_USER_ROLE (ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT)
  VALUES (2, 1, 2, 'system', systimestamp, 'system', systimestamp);
  """)
    @Test
    @WithMockPapUser(username = "quydv2")
    public void testCreateUser_failed_role_deactivated() throws Exception {
        var requestBody = """
          {
            "username": "thangnd",
            "roleIds": ["1","2"],
            "active": true,
            "reason": "reason1"
          }
        """;
        var request = MockMvcRequestBuilders.post("/users")
            .header("Content-Type", "application/json")
            .content(requestBody);
        mvc.perform(request).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.soaErrorCode").value(Constant.PREFIX_RESPONSE_CODE + ROLE_ID_NOT_EXIST.code()));
    }
}