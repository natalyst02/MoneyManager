package vn.com.mbbank.adminportal.core.model.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(chain = true)
public class UpdateWhitelistAccountRequest {
  @Size(max = 50)
  private String bankCode;
  @Size(max = 50)
  private String transferChannel;
  @Size(max = 50)
  private String accountNo;
  private Boolean active;
  @NotNull
  @Size(max = 2000)
  private String reason;
}
