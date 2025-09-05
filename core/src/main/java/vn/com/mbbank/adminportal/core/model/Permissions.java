package vn.com.mbbank.adminportal.core.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class Permissions {
  private List<PermissionCategoryInfo> permissions;
}
