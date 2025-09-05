package vn.com.mbbank.adminportal.core.model;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.codehaus.commons.nullanalysis.NotNull;

@Data
public class TransferChannelStatus {
    @NotNull
    private TransferChannel transferChannel;
    @NotNull
    private Boolean active;
}
