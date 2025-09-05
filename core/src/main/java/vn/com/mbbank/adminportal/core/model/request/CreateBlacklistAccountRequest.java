package vn.com.mbbank.adminportal.core.model.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.experimental.Accessors;
import vn.com.mbbank.adminportal.core.model.AccountEntryType;
import vn.com.mbbank.adminportal.core.model.TransactionType;

@Getter
@Accessors(chain = true)
public class CreateBlacklistAccountRequest {
  @NotEmpty
  @Pattern(regexp = "^[a-zA-Z0-9]+$")
  @Size(max = 50)
  private String accountNo;
  @NotEmpty
  @Pattern(regexp = "^[a-zA-Z0-9]+$")
  @Size(max = 50)
  private String bankCode;
  @NotNull
  private AccountEntryType type;
  @NotNull
  private TransactionType transactionType;
  @NotNull
  private Boolean active;
  @NotEmpty
  @Size(max = 2000)
  private String reason;
}
