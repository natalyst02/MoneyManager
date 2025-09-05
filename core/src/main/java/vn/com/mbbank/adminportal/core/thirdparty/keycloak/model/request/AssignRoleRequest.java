package vn.com.mbbank.adminportal.core.thirdparty.keycloak.model.request;

import com.dslplatform.json.CompiledJson;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@CompiledJson
@Accessors(chain = true)
public class AssignRoleRequest {
  private String id;
  private String name;
  private boolean composite;
  private boolean clientRole;
  private String containerId;
}
