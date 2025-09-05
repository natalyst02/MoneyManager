package vn.com.mbbank.adminportal.core.thirdparty.routingtransfer.model;

import com.dslplatform.json.CompiledJson;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@CompiledJson
public class BranchInfo {
  private String branchCode;
  private String branchName;
  private String city;
  private String type;
}
