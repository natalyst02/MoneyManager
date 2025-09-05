package vn.com.mbbank.adminportal.core.model.filter;

import lombok.Data;
import lombok.experimental.Accessors;
import vn.com.mbbank.adminportal.core.model.TransferChannel;

import java.time.OffsetDateTime;

@Data
@Accessors(chain = true)
public class TransferChannelLimitHistoryFilter {
    private Long transferChannelId;
    private String updatedBy;
    private OffsetDateTime updatedAtFrom;
    private OffsetDateTime updatedAtTo;
}
