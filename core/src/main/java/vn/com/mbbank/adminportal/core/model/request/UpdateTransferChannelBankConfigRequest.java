package vn.com.mbbank.adminportal.core.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import vn.com.mbbank.adminportal.core.model.TransferChannel;

@Getter
@Setter
@Accessors(chain = true)
public class UpdateTransferChannelBankConfigRequest {
  private Long id;
  @NotBlank
  @Size(max = 2000)
  private String reason;
  @NotNull
  private Boolean active;
}
