package vn.com.mbbank.adminportal.core.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;
import vn.com.mbbank.adminportal.core.model.ApprovalStatus;
import vn.com.mbbank.adminportal.core.model.TransferChannel;

import java.time.OffsetDateTime;

@Data
@Accessors(chain = true)
@Table(name = "PAP_WHITELIST_ACCOUNT_HISTORY")
@Entity
public class WhitelistAccountHistory {
  @Id
  @SequenceGenerator(name = "WHITELIST_ACCOUNT_HISTORY_ID_SEQ", sequenceName = "WHITELIST_ACCOUNT_HISTORY_ID_SEQ", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WHITELIST_ACCOUNT_HISTORY_ID_SEQ")
  Long id;
  String action;
  Long whitelistAccountId;
  String bankCode;
  @Enumerated(EnumType.STRING)
  TransferChannel transferChannel;
  String accountNo;
  String reason;

  @Enumerated(EnumType.STRING)
  ApprovalStatus approvalStatus;

  boolean active;
  OffsetDateTime createdAt;
  String createdBy;
  OffsetDateTime updatedAt;
  String updatedBy;
  OffsetDateTime approvedAt;
  String approvedBy;
}