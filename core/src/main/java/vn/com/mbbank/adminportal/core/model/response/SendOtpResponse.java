package vn.com.mbbank.adminportal.core.model.response;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SendOtpResponse {
  private String otpKey;
  private String otpTime;
}
