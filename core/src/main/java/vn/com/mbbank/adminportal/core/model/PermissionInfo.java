package vn.com.mbbank.adminportal.core.model;

import com.dslplatform.json.CompiledJson;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@CompiledJson
public class PermissionInfo {
  private Long id;
  private String module;
  private String action;
}
