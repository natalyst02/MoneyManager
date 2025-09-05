package vn.com.mbbank.adminportal.core.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;
import vn.com.mbbank.adminportal.core.model.AccountEntryType;
import vn.com.mbbank.adminportal.core.model.TransactionType;

import java.time.OffsetDateTime;

@Data
@Accessors(chain = true)
@Table(name = "PAP_BLACKLIST_ACCOUNT_HISTORY")
@Entity
public class BlacklistAccountHistory {
  @Id
  @SequenceGenerator(name = "BLACKLIST_ACCOUNT_HISTORY_ID_SEQ", sequenceName = "BLACKLIST_ACCOUNT_HISTORY_ID_SEQ", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BLACKLIST_ACCOUNT_HISTORY_ID_SEQ")
  Long id;
  String action;
  Long blacklistAccountId;
  String accountNo;
  String bankCode;

  @Enumerated(EnumType.STRING)
  AccountEntryType type;
  @Enumerated(EnumType.STRING)
  TransactionType transactionType;

  String reason;
  boolean active;
  OffsetDateTime createdAt;
  String createdBy;
  OffsetDateTime updatedAt;
  String updatedBy;
}
