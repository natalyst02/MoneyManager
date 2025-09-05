package vn.com.mbbank.adminportal.core.model.response;

import com.dslplatform.json.CompiledJson;
import lombok.Data;
import lombok.experimental.Accessors;
import vn.com.mbbank.adminportal.core.model.ApprovalStatus;

import java.time.OffsetDateTime;

@Data
@Accessors(chain = true)
@CompiledJson
public class UpdateAliasAccountResp {
  private Long id;
  private String name;
  private String partnerType;
  private boolean active;
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
  private String reason;
  private Boolean isRetryConfirm;
  private ApprovalStatus approvalStatus;
  private String approvedBy;
  private OffsetDateTime approvedAt;
  private OffsetDateTime createdAt;
  private String createdBy;
  private OffsetDateTime updatedAt;
  private String updatedBy;
}
