package vn.com.mbbank.adminportal.core.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;
import vn.com.mbbank.adminportal.core.model.ApprovalStatus;
import vn.com.mbbank.adminportal.core.model.PartnerType;

import java.time.OffsetDateTime;

@Data
@Accessors(chain = true)
@Table(name = "PAP_ALIAS_ACCOUNT")
@Entity
public class AliasAccount {
  @Id
  @SequenceGenerator(name = "PAP_ALIAS_ACCOUNT_ID_SEQ", sequenceName = "PAP_ALIAS_ACCOUNT_ID_SEQ", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PAP_ALIAS_ACCOUNT_ID_SEQ")
  Long id;
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

  public AliasAccount shallowCopy() {
    return new AliasAccount()
        .setId(this.id)
        .setName(this.name)
        .setPartnerType(this.partnerType)
        .setPartnerAccount(this.partnerAccount)
        .setGetNameUrl(this.getNameUrl)
        .setConfirmUrl(this.confirmUrl)
        .setProtocol(this.protocol)
        .setChannel(this.channel)
        .setRegex(this.regex)
        .setMinTransLimit(this.minTransLimit)
        .setMaxTransLimit(this.maxTransLimit)
        .setPartnerPublicKey(this.partnerPublicKey)
        .setMbPrivateKey(this.mbPrivateKey)
        .setIsRetryConfirm(this.isRetryConfirm)
        .setReason(this.reason)
        .setActive(this.active)
        .setApprovalStatus(this.approvalStatus)
        .setApprovedBy(this.approvedBy)
        .setApprovedAt(this.approvedAt)
        .setCreatedAt(this.createdAt)
        .setCreatedBy(this.createdBy)
        .setUpdatedAt(this.updatedAt)
        .setUpdatedBy(this.updatedBy);
  }
}
