package vn.com.mbbank.adminportal.core.model.response;

import lombok.Data;
import lombok.experimental.Accessors;
import vn.com.mbbank.adminportal.core.model.TransferChannel;
import vn.com.mbbank.adminportal.core.model.TransferType;

import java.time.OffsetDateTime;

@Data
@Accessors(chain = true)
public class GetTransferChannelConfigResponse {
  private long id;
  private TransferChannel transferChannel;
  private int priority;
  private TransferType transferType;
  private boolean active;
  private String reason;
  private OffsetDateTime createdAt;
  private String createdBy;
  private OffsetDateTime updatedAt;
  private String updatedBy;
}
