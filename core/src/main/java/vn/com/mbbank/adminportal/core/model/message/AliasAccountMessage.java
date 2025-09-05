package vn.com.mbbank.adminportal.core.model.message;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;

@Data
@Accessors(chain = true)
public class AliasAccountMessage {
  private String name;
  private String partnerType;
  private Boolean active;
  private String partnerAccount;
  private String protocol;
  private String channel;
  private String getNameUrl;
  private String confirmUrl;
  private String regex;
  private Long minTransLimit;
  private Long maxTransLimit;
  private String partnerPublicKey;
  private String mbPrivateKey;
  Boolean isRetryConfirm;
  private String reason;
  private OffsetDateTime createdAt;
  private String createdBy;
  private OffsetDateTime updatedAt;
  private String updatedBy;
}
