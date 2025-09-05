package vn.com.mbbank.adminportal.common.model;

import com.dslplatform.json.CompiledJson;
import com.dslplatform.json.JsonAttribute;
import lombok.Data;
import lombok.experimental.Accessors;

@CompiledJson
@Data
@Accessors(chain = true)
public class MBStaff implements User {
  @JsonAttribute(alternativeNames = {"preferred_username", "user_name"})
  private String username;
  @JsonAttribute(name = "exp")
  private Long tokenExpiredAt;
  @JsonAttribute(name = "iat")
  private Long tokenIssuedAt;
  private String scope;
  @JsonAttribute(name = "client_group")
  private String sourceAppId;
  @JsonAttribute(name = "session_state")
  private String sessionState;
}
