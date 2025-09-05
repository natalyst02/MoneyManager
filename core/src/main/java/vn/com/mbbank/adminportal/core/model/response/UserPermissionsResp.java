package vn.com.mbbank.adminportal.core.model.response;

import com.dslplatform.json.CompiledJson;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

@Data
@Accessors(chain = true)
@CompiledJson
public class UserPermissionsResp {
  private String module;
  private Set<String> actions;
}
