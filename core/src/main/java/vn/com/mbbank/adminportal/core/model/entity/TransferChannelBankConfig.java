package vn.com.mbbank.adminportal.core.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;
import vn.com.mbbank.adminportal.core.model.TransferChannel;

import java.time.OffsetDateTime;

@Data
@Accessors(chain = true)
@Table(name = "PAP_TRANSFER_CHANNEL_BANK_CONFIG")
@Entity
public class TransferChannelBankConfig {
  @Id
  @SequenceGenerator(name = "PAP_TRANSFER_CHANNEL_BANK_CONFIG_ID_SEQ", sequenceName = "PAP_TRANSFER_CHANNEL_BANK_CONFIG_ID_SEQ", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PAP_TRANSFER_CHANNEL_BANK_CONFIG_ID_SEQ")
  Long id;
  String bankCode;
  String cardBin;
  @Enumerated(EnumType.STRING)
  TransferChannel transferChannel;
  String reason;
  Boolean active;
  OffsetDateTime createdAt;
  String createdBy;
  OffsetDateTime updatedAt;
  String updatedBy;
}
