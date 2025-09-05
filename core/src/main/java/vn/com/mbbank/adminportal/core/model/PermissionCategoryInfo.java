package vn.com.mbbank.adminportal.core.model;

import com.dslplatform.json.CompiledJson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;

@Data
@Accessors(chain = true)
@CompiledJson
@AllArgsConstructor
@NoArgsConstructor
public class PermissionCategoryInfo {
  private Long id;
  private PermissionType type;
  private String subType;
  private String module;
  private String action;
  private int bitmaskValue;
  private String description;
  private OffsetDateTime createdAt;
}
