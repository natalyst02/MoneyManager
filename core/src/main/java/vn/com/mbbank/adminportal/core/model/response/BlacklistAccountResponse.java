package vn.com.mbbank.adminportal.core.model.response;

import com.dslplatform.json.CompiledJson;
import lombok.Data;
import lombok.experimental.Accessors;
import vn.com.mbbank.adminportal.core.model.AccountEntryType;
import vn.com.mbbank.adminportal.core.model.TransactionType;

import java.time.OffsetDateTime;

@Data
@Accessors(chain = true)
@CompiledJson
public class BlacklistAccountResponse {
  private Long id;
  private String accountNo;
  private String bankCode;
  private AccountEntryType type;
  private TransactionType transactionType;
  private String reason;
  private Boolean active;
  private OffsetDateTime createdAt;
  private String createdBy;
  private OffsetDateTime updatedAt;
  private String updatedBy;
}
