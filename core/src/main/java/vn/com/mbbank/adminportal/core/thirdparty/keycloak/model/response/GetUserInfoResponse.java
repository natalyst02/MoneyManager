package vn.com.mbbank.adminportal.core.thirdparty.keycloak.model.response;

import com.dslplatform.json.CompiledJson;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
@CompiledJson
public class GetUserInfoResponse {
  private String id;
  private String username;
  private boolean enabled;
}
