package vn.com.mbbank.adminportal.core.model.message;

import com.dslplatform.json.CompiledJson;
import lombok.Data;
import lombok.experimental.Accessors;
import vn.com.mbbank.adminportal.core.model.TransferChannel;

import java.time.OffsetDateTime;

@Data
@CompiledJson
@Accessors(chain = true)
public class UpdateTransferChannelConfigStatusMessage {
  private TransferChannel transferChannel;
  private boolean active;
  private String updatedBy;
  private OffsetDateTime updatedAt;
}
