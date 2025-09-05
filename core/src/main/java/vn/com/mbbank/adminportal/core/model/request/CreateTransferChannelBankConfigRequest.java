package vn.com.mbbank.adminportal.core.model.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import vn.com.mbbank.adminportal.core.model.TransferChannel;
import vn.com.mbbank.adminportal.core.model.TransferChannelStatus;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class CreateTransferChannelBankConfigRequest {
  @NotNull
  @Pattern(regexp = "^[a-zA-Z0-9]+$")
  @Size(max = 50)
  private String bankCode;
  @Size(max = 50)
  @Pattern(regexp = "^[a-zA-Z0-9]+$")
  private String cardBin;
  @NotEmpty
  private List<TransferChannelStatus> transferChannels;
  @Size(max = 2000)
  private String reason;
}
