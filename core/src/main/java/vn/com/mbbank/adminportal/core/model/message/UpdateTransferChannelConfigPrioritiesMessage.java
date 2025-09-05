package vn.com.mbbank.adminportal.core.model.message;

import com.dslplatform.json.CompiledJson;
import lombok.Data;
import lombok.experimental.Accessors;
import vn.com.mbbank.adminportal.core.model.TransferChannelPriority;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Accessors(chain = true)
@CompiledJson
public class UpdateTransferChannelConfigPrioritiesMessage {
  private List<TransferChannelPriority> transferChannelPriorities;
  private OffsetDateTime updatedAt;
  private String updatedBy;
}
