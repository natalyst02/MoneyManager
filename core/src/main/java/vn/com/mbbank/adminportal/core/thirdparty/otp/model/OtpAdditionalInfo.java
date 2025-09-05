package vn.com.mbbank.adminportal.core.thirdparty.otp.model;

import com.dslplatform.json.CompiledJson;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@CompiledJson
public class OtpAdditionalInfo {
  private String name;
  private String value;
}
