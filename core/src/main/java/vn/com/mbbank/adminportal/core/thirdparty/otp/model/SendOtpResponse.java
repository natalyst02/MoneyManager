package vn.com.mbbank.adminportal.core.thirdparty.otp.model;

import com.dslplatform.json.CompiledJson;
import lombok.Data;

import java.util.List;

@Data
@CompiledJson
public class SendOtpResponse {
  private String errorCode;
  private String otpTime;
  private String otpType;
  private List<OtpAdditionalInfo> additionalInfos;
}
