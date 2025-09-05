package vn.com.mbbank.adminportal.core.model.request;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RoutingBankRequest {
  private Boolean showBranch;
  private String bankCode;
}
