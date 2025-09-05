package vn.com.mbbank.adminportal.core.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;
import vn.com.mbbank.adminportal.core.model.TransferChannel;

import java.time.OffsetDateTime;

@Data
@Accessors(chain = true)
@Table(name = "PAP_TRANSFER_CHANNEL_CONFIG")
@Entity
public class TransferChannelConfig {
  @Id
  @SequenceGenerator(name = "TRANSFER_CHANNEL_CONFIG_ID_SEQ", sequenceName = "TRANSFER_CHANNEL_CONFIG_ID_SEQ", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TRANSFER_CHANNEL_CONFIG_ID_SEQ")
  Long id;
  @Enumerated(EnumType.STRING)
  TransferChannel transferChannel;
  int priority;
  String reason;
  boolean active;
  OffsetDateTime createdAt;
  String createdBy;
  OffsetDateTime updatedAt;
  String updatedBy;
}
