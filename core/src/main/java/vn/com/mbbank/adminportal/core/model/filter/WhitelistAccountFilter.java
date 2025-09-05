package vn.com.mbbank.adminportal.core.model.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import vn.com.mbbank.adminportal.core.model.ApprovalStatus;
import vn.com.mbbank.adminportal.core.model.TransferChannel;

@Getter
@AllArgsConstructor
@Accessors(chain = true)
public class WhitelistAccountFilter {
    private String accountNo;
    private String bankCode;
    private TransferChannel transferChannel;
    private Boolean active;
    private ApprovalStatus approvalStatus;
}