package vn.com.mbbank.adminportal.core.thirdparty.otp.model;

import com.dslplatform.json.CompiledJson;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@CompiledJson
@Accessors(chain = true)
public class VerifyOtpResponse {
  private boolean isVerify;
}
