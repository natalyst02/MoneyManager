package vn.com.mbbank.adminportal.core.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;
import vn.com.mbbank.adminportal.core.model.TransferChannel;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Accessors(chain = true)
@Table(name = "PAP_TRANSFER_CHANNEL_LIMIT")
@Entity
public class TransferChannelLimit {
    @Id
    @SequenceGenerator(name = "PAP_TRANSFER_CHANNEL_LIMIT_ID_SEQ", sequenceName = "PAP_TRANSFER_CHANNEL_LIMIT_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PAP_TRANSFER_CHANNEL_LIMIT_ID_SEQ")
    Long id;

    @Enumerated(EnumType.STRING)
    TransferChannel transferChannel;
    BigDecimal maxAmount;
    BigDecimal minAmount;
    String reason;
    Boolean active;
    OffsetDateTime createdAt;
    String createdBy;
    OffsetDateTime updatedAt;
    String updatedBy;
    BigDecimal fragmentMaxAmount;
    BigDecimal fragmentAmount;
}
