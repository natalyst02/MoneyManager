package vn.com.mbbank.adminportal.core.model.response;

import lombok.Data;
import lombok.experimental.Accessors;
import vn.com.mbbank.adminportal.core.model.PermissionInfo;
import vn.com.mbbank.adminportal.core.model.RoleType;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Data
@Accessors(chain = true)
public class RoleResponse {
  private String id;
  private String code;
  private String name;
  private RoleType type;
  private String description;
  private Map<String, List<PermissionInfo>> permissions;
  private String reason;
  private boolean active;
  private OffsetDateTime createdAt;
  private String createdBy;
}
