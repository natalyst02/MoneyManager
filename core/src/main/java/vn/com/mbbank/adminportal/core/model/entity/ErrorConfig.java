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

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Accessors(chain = true)
@Table(name = "PAP_ERROR_CONFIG")
@Entity
public class ErrorConfig {
  @Id
  @SequenceGenerator(name = "PAP_ERROR_CONFIG_ID_SEQ", sequenceName = "PAP_ERROR_CONFIG_ID_SEQ", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PAP_ERROR_CONFIG_ID_SEQ")
  Long id;
  @Enumerated(EnumType.STRING)
  TransferChannel transferChannel;
  String bankCode;
  String bankName;
  BigDecimal maxErrorPercentage;
  long minTxn;
  long calculationFrequency;
  long offlineDuration;
  String reason;
  Boolean active;
  OffsetDateTime createdAt;
  String createdBy;
  OffsetDateTime updatedAt;
  String updatedBy;
}
