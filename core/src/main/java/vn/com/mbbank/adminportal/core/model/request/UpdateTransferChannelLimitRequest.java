package vn.com.mbbank.adminportal.core.model.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.experimental.Accessors;
import vn.com.mbbank.adminportal.core.model.TransferChannel;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class UpdateTransferChannelLimitRequest {
    private TransferChannel transferChannel;
    @NotNull
    @Positive
    @Digits(integer = 18, fraction = 0)
    private BigDecimal minAmount;

    @NotNull
    @Positive
    @Digits(integer = 18, fraction = 0)
    private BigDecimal maxAmount;


    @Positive
    @Digits(integer = 18, fraction = 0)
    private BigDecimal fragmentMaxAmount;


    @Positive
    @Digits(integer = 18, fraction = 0)
    private BigDecimal fragmentAmount;

    @NotNull
    private Boolean active;

    @NotBlank
    @Size(max = 2000)
    private String reason;
}
