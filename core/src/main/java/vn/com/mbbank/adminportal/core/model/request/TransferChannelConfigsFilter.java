package vn.com.mbbank.adminportal.core.model.request;

import lombok.Data;
import lombok.experimental.Accessors;
import vn.com.mbbank.adminportal.core.model.TransferChannel;
import vn.com.mbbank.adminportal.core.model.TransferType;

@Data
@Accessors(chain = true)
public class TransferChannelConfigsFilter {
  private TransferChannel transferChannel;
  private TransferType transferType;
}
