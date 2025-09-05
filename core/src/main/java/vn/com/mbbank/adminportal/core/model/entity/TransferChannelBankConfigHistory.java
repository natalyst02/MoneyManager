package vn.com.mbbank.adminportal.core.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;
import vn.com.mbbank.adminportal.core.model.Action;
import vn.com.mbbank.adminportal.core.model.TransferChannel;

import java.time.OffsetDateTime;

@Data
@Accessors(chain = true)
@Table(name = "PAP_TRANSFER_CHANNEL_BANK_CONFIG_HISTORY")
@Entity
public class TransferChannelBankConfigHistory {
  @Id
  @SequenceGenerator(name = "PAP_TRANSFER_CHANNEL_BANK_CONFIG_HISTORY_ID_SEQ", sequenceName = "PAP_TRANSFER_CHANNEL_BANK_CONFIG_HISTORY_ID_SEQ", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PAP_TRANSFER_CHANNEL_BANK_CONFIG_HISTORY_ID_SEQ")
  Long id;
  @Enumerated(EnumType.STRING)
  Action action;
  Long transferChannelBankConfigId;
  String bankCode;
  String cardBin;
  @Enumerated(EnumType.STRING)
  TransferChannel transferChannel;
  String reason;
  boolean active;
  OffsetDateTime createdAt;
  String createdBy;
  OffsetDateTime updatedAt;
  String updatedBy;
}
