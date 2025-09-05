package vn.com.mbbank.adminportal.core.model;

import com.dslplatform.json.CompiledJson;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@CompiledJson
public class Branch {
  private String branchCode;
  private String branchName;
  private String city;
  private String type;
}
