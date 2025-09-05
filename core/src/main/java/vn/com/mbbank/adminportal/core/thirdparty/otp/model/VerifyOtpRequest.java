package vn.com.mbbank.adminportal.core.thirdparty.otp.model;

import com.dslplatform.json.CompiledJson;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@CompiledJson
public class VerifyOtpRequest {
  private Metadata metadata;

  private String otpType;
  private String otpKey;
  private String otpValue;

  @Data
  public static class Metadata {
    @JsonProperty("USER_AD")
    private String userAd;

  }
}
