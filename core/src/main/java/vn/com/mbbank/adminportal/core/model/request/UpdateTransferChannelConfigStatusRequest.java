package vn.com.mbbank.adminportal.core.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UpdateTransferChannelConfigStatusRequest {
  @NotNull
  private Long id;
  @NotNull
  private Boolean active;
  @NotBlank
  @Size(max = 2000)
  private String reason;
}
