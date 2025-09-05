package vn.com.mbbank.adminportal.core.model.response;

import lombok.Data;
import lombok.experimental.Accessors;
import vn.com.mbbank.adminportal.core.model.PriorityReference;
import vn.com.mbbank.adminportal.core.model.TransferChannelReference;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Accessors(chain = true)
public class UpdateTransferChannelConfigPrioritiesResponse {
  private List<PriorityReference> priorities;
  private String reason;
  private String updatedBy;
  private OffsetDateTime updatedAt;
}