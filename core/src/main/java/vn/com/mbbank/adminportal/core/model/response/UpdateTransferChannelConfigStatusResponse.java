package vn.com.mbbank.adminportal.core.model.response;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;

@Data
@Accessors(chain = true)
public class UpdateTransferChannelConfigStatusResponse {
  private Long id;
  private Boolean active;
  private String reason;
  private OffsetDateTime updatedAt;
  private String updatedBy;
}
