package vn.com.mbbank.adminportal.core.model.response;

import com.dslplatform.json.CompiledJson;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Accessors(chain = true)
@CompiledJson
public class TransferChannelLimitHistoryResp {
    private Long id;
    private String action;
    private Long transferChannelId;
    private String transferChannel;
    private BigDecimal maxAmount;
    private BigDecimal minAmount;
    private BigDecimal fragmentAmount;
    private BigDecimal fragmentMaxAmount;
    private String reason;
    private boolean active;
    private String createdBy;
    private OffsetDateTime createdAt;
    private String updatedBy;
    private OffsetDateTime updatedAt;
}
