package vn.com.mbbank.adminportal.core.model.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.experimental.Accessors;
import vn.com.mbbank.adminportal.core.model.PartnerType;

@Getter
@Accessors(chain = true)
public class UpdateAliasAccountRequest {
  @NotNull
  private PartnerType partnerType;
  @Pattern(regexp = "^[A-Z0-9-_]+$")
  @Size(max = 50)
  private String partnerAccount;
  @Size(max = 200)
  private String getNameUrl;
  @Size(max = 200)
  private String confirmUrl;
  @Size(max = 50)
  private String protocol;
  @Size(max = 10)
  private String channel;
  @NotNull
  @NotEmpty
  @Size(max = 50)
  private String regex;
  private Long minTransLimit;
  private Long maxTransLimit;
  @Size(max = 2000)
  private String partnerPublicKey;
  @Size(max = 2000)
  private String mbPrivateKey;
  @NotNull
  @NotEmpty
  @Size(max = 2000)
  private String reason;
  @NotNull
  private Boolean active;
  @NotNull
  private Boolean isRetryConfirm;
}
