package vn.com.mbbank.adminportal.core.model.message;

import lombok.Data;
import lombok.experimental.Accessors;
import vn.com.mbbank.adminportal.core.model.TransferChannel;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Accessors(chain = true)
public class TransferChannelLimitMessage {
    private TransferChannel transferChannel;
    private BigDecimal maxAmount;
    private BigDecimal minAmount;
    private String reason;
    private Boolean active;
    private OffsetDateTime createdAt;
    private String createdBy;
    private OffsetDateTime updatedAt;
    private String updatedBy;
    private BigDecimal fragmentMaxAmount;
    private BigDecimal fragmentAmount;
}
