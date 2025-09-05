package vn.com.mbbank.adminportal.core.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TransferChannelPriority {
  private TransferChannel transferChannel;
  private Integer priority;
}
