package vn.com.mbbank.adminportal.core.model.request;

import com.dslplatform.json.CompiledJson;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@CompiledJson
@Accessors(chain = true)
public class VerifyOtpRequest {
  @NotBlank(message = "otpKey is invalid")
  private String otpKey;
  @NotBlank(message = "otpValue is invalid")
  @Size(min = 6, max = 6, message = "otpValue must be exactly 6 characters")
  private String otpValue;

  @JsonCreator
  public VerifyOtpRequest(
      @JsonProperty("otpKey") String otpKey,
      @JsonProperty("otpValue") String otpValue) {
    this.otpKey = otpKey;
    this.otpValue = otpValue;
  }

}
