package vn.com.mbbank.adminportal.core.thirdparty.routingtransfer.model.response;

import com.dslplatform.json.CompiledJson;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import vn.com.mbbank.adminportal.core.thirdparty.routingtransfer.model.BranchInfo;

import java.util.List;
@Data
@Accessors(chain = true)
@CompiledJson
public class GetBanksResponse {
  private String bankCode;
  private String shortName;
  private String fullName;
  @JsonProperty("branchList")
  private List<BranchInfo> branches;
}
