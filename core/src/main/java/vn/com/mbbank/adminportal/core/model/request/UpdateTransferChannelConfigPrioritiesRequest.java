package vn.com.mbbank.adminportal.core.model.request;

import lombok.Data;
import lombok.experimental.Accessors;
import vn.com.mbbank.adminportal.core.model.PriorityReference;

import java.util.List;

@Data
@Accessors(chain = true)
public class UpdateTransferChannelConfigPrioritiesRequest {
  private List<PriorityReference> priorities;
  private String reason;
}
