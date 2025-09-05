package vn.com.mbbank.adminportal.core.model.response;

import lombok.Data;
import lombok.experimental.Accessors;
import vn.com.mbbank.adminportal.core.model.RoleType;

import java.time.OffsetDateTime;
import java.util.Map;

@Data
@Accessors(chain = true)
public class UpdateRoleResponse {
  private String code;
  private String name;
  private RoleType type;
  private String description;
  private Map<String, Integer> permissions;
  private String reason;
  private boolean active;
  private OffsetDateTime createdAt;
  private String createdBy;
}
