package vn.com.mbbank.adminportal.core.thirdparty.keycloak.model.response;

import com.dslplatform.json.CompiledJson;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@CompiledJson
public class ValidateTokenResponse {
  boolean active;
}
