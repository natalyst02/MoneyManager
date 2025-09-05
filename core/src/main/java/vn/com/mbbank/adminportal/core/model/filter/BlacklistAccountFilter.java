package vn.com.mbbank.adminportal.core.model.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import vn.com.mbbank.adminportal.core.model.AccountEntryType;
import vn.com.mbbank.adminportal.core.model.TransactionType;

@Getter
@AllArgsConstructor
@Accessors(chain = true)
public class BlacklistAccountFilter {
  private String accountNo;
  private String bankCode;
  private AccountEntryType type;
  private TransactionType transactionType;
  private Boolean active;
}
