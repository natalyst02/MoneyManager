package vn.com.mbbank.adminportal.core.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PermissionAction {
  private Long id;
  private String action;
  private boolean selected;
}
