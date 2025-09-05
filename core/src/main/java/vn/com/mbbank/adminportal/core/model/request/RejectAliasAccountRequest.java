package vn.com.mbbank.adminportal.core.model.request;

import com.dslplatform.json.CompiledJson;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@CompiledJson
public class RejectAliasAccountRequest {
  @NotNull
  @NotEmpty
  @Size(max = 2000)
  private String reason;
}
