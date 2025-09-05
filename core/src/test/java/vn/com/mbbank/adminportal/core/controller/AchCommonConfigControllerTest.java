package vn.com.mbbank.adminportal.core.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import vn.com.mbbank.adminportal.common.ApplicationTest;
import vn.com.mbbank.adminportal.common.SqlStatementTest;
import vn.com.mbbank.adminportal.common.WithMockPapUser;
import vn.com.mbbank.adminportal.common.util.Constant;

import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static vn.com.mbbank.adminportal.common.util.CommonErrorCode.FORBIDDEN;
import static vn.com.mbbank.adminportal.common.util.CommonErrorCode.UNAUTHORIZED;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AchCommonConfigControllerTest extends ApplicationTest {
  @Autowired
  private MockMvc mvc;

  private static String random;

  @BeforeAll
  static void setUp() {
    random = UUID.randomUUID().toString();
  }

  private final String STATEMENTS = SqlStatementTest.PERMISSIONS + """
      truncate table  PAP_ROLE;
      truncate table PAP_USER;
      """;

  @WithMockPapUser(username = "quydv2", permissions = """
       {
        "role": 31,
        "user": 31,
        "permission": 1,
        "ach-common-config": 7
      }
      """)
  @Sql(statements = STATEMENTS)
  @Test
  @Order(1)
  void forward_createAchCommonConfig_Success() throws Exception {
    var request = MockMvcRequestBuilders.post("/ach/common-config")
        .header("Content-Type", "application/json")
        .content("""
            {
                "createdBy": "test",
                "reason": "like",
                "isActive": "N",
                "functionCode": "%s",
                "key": "%s",
                "value": "10",
                "description": "12@$g"
            }            
            """.formatted(random, random));
    var mvcResult = mvc.perform(request)
        .andExpect(request().asyncStarted())
        .andReturn();
    mvc.perform(MockMvcRequestBuilders.asyncDispatch(mvcResult))
        .andExpect(status().isOk());
  }

  @Sql(statements = STATEMENTS)
  @WithMockPapUser(username = "quydv2", permissions = """
       {
        "role": 31,
        "user": 31,
        "permission": 1,
        "ach-common-config": 7
      }
      """)
  @Test
  @Order(2)
  void forward_getAllCommonConfig_Success() throws Exception {
    var request = MockMvcRequestBuilders.get("/ach/common-config");
    var mvcResult = mvc.perform(request)
        .andExpect(request().asyncStarted())
        .andReturn();
    mvc.perform(MockMvcRequestBuilders.asyncDispatch(mvcResult))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isNotEmpty());
  }

  @WithMockPapUser(username = "quydv2", permissions = """
       {
        "role": 31,
        "user": 31,
        "permission": 1,
        "ach-common-config": 7
      }
      """)
  @Sql(statements = STATEMENTS)
  @Test
  @Order(3)
  void forward_getCommonConfig_Success() throws Exception {
    var request = MockMvcRequestBuilders.get("/ach/common-config/get?functionCode=" + random + "&key=" + random);
    var mvcResult = mvc.perform(request)
        .andExpect(request().asyncStarted())
        .andReturn();
    mvc.perform(MockMvcRequestBuilders.asyncDispatch(mvcResult))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isNotEmpty())
        .andExpect(jsonPath("$.data.functionCode").value(random))
        .andExpect(jsonPath("$.data.key").value(random))
        .andExpect(jsonPath("$.data.value").value("10"));
  }

  @WithMockPapUser(username = "quydv2", permissions = """
       {
        "role": 31,
        "user": 31,
        "permission": 1,
        "ach-common-config": 7
      }
      """)
  @Sql(statements = STATEMENTS)
  @Test
  @Order(4)
  void forward_updateAchCommonConfig_Success() throws Exception {
    var request = MockMvcRequestBuilders.put("/ach/common-config")
        .header("Content-Type", "application/json")
        .content("""
            {
                "createdBy": "test",
                "reason": "like",
                "isActive": "N",
                "functionCode": "%s",
                "key": "%s",
                "value": "10",
                "description": "12@$g"
            }       
            """.formatted(random, random));
    var mvcResult = mvc.perform(request)
        .andExpect(request().asyncStarted())
        .andReturn();
    mvc.perform(MockMvcRequestBuilders.asyncDispatch(mvcResult))
        .andExpect(status().isOk());
  }

  @Sql(statements = STATEMENTS)
  @Test
  void forwardGet_withoutSignIn_ThenThrownUnauthorized() throws Exception {
    var request = MockMvcRequestBuilders.get("/ach/common-config");
    mvc.perform(request)
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.soaErrorCode", equalTo(Constant.PREFIX_RESPONSE_CODE + UNAUTHORIZED.code())));
  }

  @WithMockPapUser(username = "quydv2", permissions = """
       {
        "role": 31,
        "user": 31,
        "permission": 1
      }
      """)
  @Sql(statements = STATEMENTS)
  @Test
  void forwardGet_withoutPermission_ThenThrownForbidden() throws Exception {
    var request = MockMvcRequestBuilders.get("/ach/common-config");
    mvc.perform(request)
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.soaErrorCode", equalTo(Constant.PREFIX_RESPONSE_CODE + FORBIDDEN.code())));
  }

  @Sql(statements = STATEMENTS)
  @Test
  void forwardCreate_withoutSignIn_ThenThrownUnauthorized() throws Exception {
    var request = MockMvcRequestBuilders.post("/ach/common-config");
    mvc.perform(request)
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.soaErrorCode", equalTo(Constant.PREFIX_RESPONSE_CODE + UNAUTHORIZED.code())));
  }

  @WithMockPapUser(username = "quydv2", permissions = """
       {
        "role": 31,
        "user": 31,
        "permission": 1
      }
      """)
  @Sql(statements = STATEMENTS)
  @Test
  void forwardCreate_withoutPermission_ThenThrownForbidden() throws Exception {
    var request = MockMvcRequestBuilders.post("/ach/common-config");
    mvc.perform(request)
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.soaErrorCode", equalTo(Constant.PREFIX_RESPONSE_CODE + FORBIDDEN.code())));
  }

  @Sql(statements = STATEMENTS)
  @Test
  void forwardUpdate_withoutSignIn_ThenThrownUnauthorized() throws Exception {
    var request = MockMvcRequestBuilders.put("/ach/common-config");
    mvc.perform(request)
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.soaErrorCode", equalTo(Constant.PREFIX_RESPONSE_CODE + UNAUTHORIZED.code())));
  }

  @WithMockPapUser(username = "quydv2", permissions = """
       {
        "role": 31,
        "user": 31,
        "permission": 1
      }
      """)
  @Sql(statements = STATEMENTS)
  @Test
  void forwardUpdate_withoutPermission_ThenThrownForbidden() throws Exception {
    var request = MockMvcRequestBuilders.put("/ach/common-config");
    mvc.perform(request)
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.soaErrorCode", equalTo(Constant.PREFIX_RESPONSE_CODE + FORBIDDEN.code())));
  }
}
