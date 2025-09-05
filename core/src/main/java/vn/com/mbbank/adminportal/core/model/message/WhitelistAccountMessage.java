package vn.com.mbbank.adminportal.core.model.message;

import lombok.Data;
import lombok.experimental.Accessors;
import vn.com.mbbank.adminportal.core.model.ApprovalStatus;
import vn.com.mbbank.adminportal.core.model.TransferChannel;

import java.time.OffsetDateTime;

@Data
@Accessors(chain = true)
public class WhitelistAccountMessage {
  private Long id;
  private String bankCode;
  private TransferChannel transferChannel;
  private String accountNo;
  private ApprovalStatus approvalStatus;
  private Boolean active;
  private OffsetDateTime createdAt;
  private String createdBy;
  private OffsetDateTime updatedAt;
  private String updatedBy;
}
