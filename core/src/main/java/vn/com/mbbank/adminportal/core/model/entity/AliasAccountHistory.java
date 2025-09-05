package vn.com.mbbank.adminportal.core.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;
import vn.com.mbbank.adminportal.core.model.ApprovalStatus;
import vn.com.mbbank.adminportal.core.model.PartnerType;

import java.time.OffsetDateTime;

@Data
@Accessors(chain = true)
@Table(name = "PAP_ALIAS_ACCOUNT_HISTORY")
@Entity
public class AliasAccountHistory {
  @Id
  @SequenceGenerator(name = "PAP_ALIAS_ACCOUNT_HISTORY_ID_SEQ", sequenceName = "PAP_ALIAS_ACCOUNT_HISTORY_ID_SEQ", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PAP_ALIAS_ACCOUNT_HISTORY_ID_SEQ")
  Long id;
  String action;
  Long papAliasAccountId;
  String name;
  @Enumerated(EnumType.STRING)
  PartnerType partnerType;
  String partnerAccount;
  String getNameUrl;
  String confirmUrl;
  String protocol;
  String channel;
  String regex;
  Long minTransLimit;
  Long maxTransLimit;
  String partnerPublicKey;
  String mbPrivateKey;
  Boolean isRetryConfirm;
  String reason;
  Boolean active;
  @Enumerated(EnumType.STRING)
  ApprovalStatus approvalStatus;
  String approvedBy;
  OffsetDateTime approvedAt;
  OffsetDateTime createdAt;
  String createdBy;
  OffsetDateTime updatedAt;
  String updatedBy;
}
