package vn.com.mbbank.adminportal.core.model.filter;

import lombok.Data;
import lombok.experimental.Accessors;
import vn.com.mbbank.adminportal.core.model.TransferChannel;

@Data
@Accessors(chain = true)
public class TransferChannelLimitFilter {
    private TransferChannel transferChannel;
    private Boolean active;
}
