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
import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static vn.com.mbbank.adminportal.common.util.CommonErrorCode.FORBIDDEN;
import static vn.com.mbbank.adminportal.common.util.CommonErrorCode.UNAUTHORIZED;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AchTktgConfigControllerTest extends ApplicationTest {
  @Autowired
  private MockMvc mvc;

  private static String random;
  private static Long randomLong;

  @BeforeAll
  static void setUp() {
    random = UUID.randomUUID().toString();
    randomLong = ThreadLocalRandom.current().nextLong(100000000000L, 1000000000000L);
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
        "ach-tktg-config": 7
      }
      """)
  @Sql(statements = STATEMENTS)
  @Test
  @Order(1)
  void forward_createAchTktgConfig_Success() throws Exception {
    var request = MockMvcRequestBuilders.post("/ach/tktg-config")
        .header("Content-Type", "application/json")
        .content("""
            {
                "createdBy": "test",
                "reason": "like",
                "isActive": "N",
                "functionCode": "MB_TO_NAPAS_MAKE",
                "accountNumber": "%s",
                "accountName": "VCC",
                "accountType": "ACCOUNT",
                "currency": "VND",
                "value": "10",
                "description": "12@$g"
            }
            """.formatted(randomLong));
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
        "ach-tktg-config": 7
      }
      """)
  @Test
  @Order(2)
  void forward_getAllTktgConfig_Success() throws Exception {
    var request = MockMvcRequestBuilders.get("/ach/tktg-config");
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
        "ach-tktg-config": 7
      }
      """)
  @Sql(statements = STATEMENTS)
  @Test
  @Order(3)
  void forward_getTktgConfig_Success() throws Exception {
    var request = MockMvcRequestBuilders.get("/ach/tktg-config/" + "MB_TO_NAPAS_MAKE" + "/" + random);
    var mvcResult = mvc.perform(request)
        .andExpect(request().asyncStarted())
        .andReturn();
    mvc.perform(MockMvcRequestBuilders.asyncDispatch(mvcResult))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data").isNotEmpty())
        .andExpect(jsonPath("$.data.functionCode").value("MB_TO_NAPAS_MAKE"))
        .andExpect(jsonPath("$.data.accountNumber").value(random))
        .andExpect(jsonPath("$.data.isActive").value("N"));
  }

  @WithMockPapUser(username = "quydv2", permissions = """
       {
        "role": 31,
        "user": 31,
        "permission": 1,
        "ach-tktg-config": 7
      }
      """)
  @Sql(statements = STATEMENTS)
  @Test
  @Order(4)
  void forward_updateAchTktgConfig_Success() throws Exception {
    var request = MockMvcRequestBuilders.put("/ach/tktg-config")
        .header("Content-Type", "application/json")
        .content("""
            {
                "updatedBy": "test",
                "reason": "like",
                "isActive": "N",
                "functionCode": "MB_TO_NAPAS_MAKE",
                "accountNumber": "%s",
                "accountName": "VCC",
                "accountType": "ACCOUNT",
                "currency": "VND",
                "value": "10",
                "description": "12@$g"
            }       
            """.formatted(randomLong));
    var mvcResult = mvc.perform(request)
        .andExpect(request().asyncStarted())
        .andReturn();
    mvc.perform(MockMvcRequestBuilders.asyncDispatch(mvcResult))
        .andExpect(status().isOk());
  }

  @Sql(statements = STATEMENTS)
  @Test
  void forwardUpdate_withoutSignIn_ThenThrownUnauthorized() throws Exception {
    var request = MockMvcRequestBuilders.put("/ach/tktg-config");
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
    var request = MockMvcRequestBuilders.put("/ach/tktg-config");
    mvc.perform(request)
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.soaErrorCode", equalTo(Constant.PREFIX_RESPONSE_CODE + FORBIDDEN.code())));
  }
}
