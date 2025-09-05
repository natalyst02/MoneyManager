package vn.com.mbbank.adminportal.core.model.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(chain = true)
public class CreateWhitelistAccountRequest {
  @NotNull
  @NotEmpty
  @Size(max = 50)
  private String bankCode;
  @NotNull
  @NotEmpty
  @Size(max = 50)
  private String transferChannel;
  @NotNull
  @NotEmpty
  @Size(max = 50)
  private String accountNo;
  @NotNull
  private Boolean active;
  @NotNull
  @Size(max = 2000)
  private String reason;
}
