package vn.com.mbbank.adminportal.core.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;

import jakarta.persistence.*;
import vn.com.mbbank.adminportal.core.model.TransferChannel;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Accessors(chain = true)
@Table(name = "PAP_TRANSFER_CHANNEL_LIMIT_HISTORY")
@Entity
public class TransferChannelLimitHistory {
    @Id
    @SequenceGenerator(name = "TRANSFER_CHANNEL_LIMIT_HISTORY_ID_SEQ", sequenceName = "TRANSFER_CHANNEL_LIMIT_HISTORY_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TRANSFER_CHANNEL_LIMIT_HISTORY_ID_SEQ")
    Long id;
    String action;
    Long transferChannelId;
    @Enumerated(EnumType.STRING)
    TransferChannel transferChannel;
    BigDecimal maxAmount;
    BigDecimal minAmount;
    BigDecimal fragmentAmount;
    BigDecimal fragmentMaxAmount;
    String reason;
    Boolean active;
    String createdBy;
    OffsetDateTime createdAt;
    String updatedBy;
    OffsetDateTime updatedAt;

}
