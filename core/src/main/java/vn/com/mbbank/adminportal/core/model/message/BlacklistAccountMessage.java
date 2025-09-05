package vn.com.mbbank.adminportal.core.model.message;

import lombok.Data;
import lombok.experimental.Accessors;
import vn.com.mbbank.adminportal.core.model.AccountEntryType;
import vn.com.mbbank.adminportal.core.model.TransactionType;

import java.time.OffsetDateTime;

@Data
@Accessors(chain = true)
public class BlacklistAccountMessage {
  private Long id;
  private String accountNo;
  private String bankCode;
  AccountEntryType type;
  TransactionType transactionType;
  private Boolean active;
  private OffsetDateTime createdAt;
  private String createdBy;
  private OffsetDateTime updatedAt;
  private String updatedBy;
}
