package vn.com.mbbank.adminportal.core.model.response;

import lombok.Data;
import lombok.experimental.Accessors;
import vn.com.mbbank.adminportal.core.model.PermissionType;

import java.time.OffsetDateTime;

@Data
@Accessors(chain = true)
public class PermissionResponse {
  private Long id;
  private String module;
  private String action;
  private PermissionType type;
  private String subType;
  private String description;
  private OffsetDateTime createdAt;
}
