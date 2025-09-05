package vn.com.mbbank.adminportal.core.thirdparty.otp.model;

import com.dslplatform.json.CompiledJson;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@CompiledJson
@Accessors(chain = true)
public class SendOtpRequest {
    private String otpKey;
    private String otpType;
    private String otpSize;
    private List<OtpAdditionalInfo> additionalInfos;
}

