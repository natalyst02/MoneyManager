package vn.com.mbbank.adminportal.core.model.filter;

import lombok.Data;
import lombok.experimental.Accessors;
import vn.com.mbbank.adminportal.core.model.ApprovalStatus;

@Data
@Accessors(chain = true)
public class AliasAccountFilter {
  private String name;
  private Boolean active;
  private ApprovalStatus approvalStatus;
}
