package vn.com.mbbank.adminportal.core.model.response;

import com.dslplatform.json.CompiledJson;
import lombok.Data;
import lombok.experimental.Accessors;
import vn.com.mbbank.adminportal.core.model.ApprovalStatus;
import vn.com.mbbank.adminportal.core.model.TransferChannel;

import java.time.OffsetDateTime;

@Data
@Accessors(chain = true)
@CompiledJson
public class WhitelistAccountResponse {
  private Long id;
  private TransferChannel transferChannel;
  private String bankCode;
  private String accountNo;
  private ApprovalStatus approvalStatus;
  private String reason;
  private Boolean active;
  private OffsetDateTime createdAt;
  private String createdBy;
  private OffsetDateTime updatedAt;
  private String updatedBy;
  private OffsetDateTime approvedAt;
  private String approvedBy;
}
