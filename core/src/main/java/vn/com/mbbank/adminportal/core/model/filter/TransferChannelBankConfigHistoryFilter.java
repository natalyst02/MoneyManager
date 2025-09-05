package vn.com.mbbank.adminportal.core.model.filter;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;

@Getter
@Setter
@Accessors(chain = true)
public class TransferChannelBankConfigHistoryFilter {
  private Long transferChannelBankConfigId;
  private String updatedBy;
  private OffsetDateTime updatedAtFrom;
  private OffsetDateTime updatedAtTo;
  private int page = 0;
  private int size = 10;
  private String sort = "id:ASC";
}
