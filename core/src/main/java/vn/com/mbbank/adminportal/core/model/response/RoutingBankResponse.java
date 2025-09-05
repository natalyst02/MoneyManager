package vn.com.mbbank.adminportal.core.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import vn.com.mbbank.adminportal.core.model.Branch;

import java.util.List;

@Data
@Accessors(chain = true)
public class RoutingBankResponse {
  private String bankCode;
  private String shortName;
  private String fullName;
  @JsonProperty("branchList")
  private List<Branch> branches;
}
