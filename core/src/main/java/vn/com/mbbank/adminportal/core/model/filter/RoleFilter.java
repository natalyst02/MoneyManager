package vn.com.mbbank.adminportal.core.model.filter;

import lombok.Data;
import lombok.experimental.Accessors;
import vn.com.mbbank.adminportal.core.model.RoleType;

import java.util.List;

@Data
@Accessors(chain = true)
public class RoleFilter {
  private String code;
  private String name;
  private List<RoleType> type;
  private Boolean active;
}
