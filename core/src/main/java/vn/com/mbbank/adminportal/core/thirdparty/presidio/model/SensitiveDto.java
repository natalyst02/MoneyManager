package vn.com.mbbank.adminportal.core.thirdparty.presidio.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SensitiveDto {
    @JsonProperty("sensitive_data")
    private String sensitiveData;
    private String position;
}
