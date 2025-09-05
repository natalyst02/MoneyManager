package vn.com.mbbank.adminportal.core.model.response;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import vn.com.mbbank.adminportal.core.model.Action;
import vn.com.mbbank.adminportal.core.model.TransferChannel;

import java.time.OffsetDateTime;

@Getter
@Setter
@Accessors(chain = true)
public class TransferChannelBankConfigHistoryResponse {
  private Long id;
  private Action action;
  private Long transferChannelBankConfigId;
  private String bankCode;
  private String cardBin;
  private TransferChannel transferChannel;
  private String reason;
  private boolean active;
  private OffsetDateTime createdAt;
  private String createdBy;
  private OffsetDateTime updatedAt;
  private String updatedBy;
}
