package vn.com.mbbank.adminportal.core.model.filter;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;

@Data
@Accessors(chain = true)
public class AliasAccountHistoryFilter {
    private Long aliasAccountId;
    private String updatedBy;
    private OffsetDateTime updatedAtFrom;
    private OffsetDateTime updatedAtTo;
}