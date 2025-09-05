package vn.com.mbbank.adminportal.core.model.filter;

import com.dslplatform.json.CompiledJson;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@CompiledJson
public class BankFilter {
  @Size(max = 50)
  private String code;
  @Size(max = 250)
  private String shortName;
  @Size(max = 500)
  private String fullName;
  private Boolean active;
}
