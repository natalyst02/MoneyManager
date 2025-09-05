package vn.com.mbbank.adminportal.core.model.request;

import com.dslplatform.json.CompiledJson;
import lombok.Data;

@Data
@CompiledJson
public class OtpRequest {
  private String otpKey;
}
