package vn.com.mbbank.adminportal.core.model.response;

import com.dslplatform.json.CompiledJson;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Accessors(chain = true)
@CompiledJson
public class TransferChannelLimitResp {
    private Long id;
    private String transferChannel;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private BigDecimal fragmentAmount;
    private BigDecimal fragmentMaxAmount;
    private boolean active;
    private String reason;
    private OffsetDateTime createdAt;
    private String createdBy;
    private OffsetDateTime updatedAt;
    private String updatedBy;
}
