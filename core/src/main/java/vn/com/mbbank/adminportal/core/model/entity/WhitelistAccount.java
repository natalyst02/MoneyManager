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
import vn.com.mbbank.adminportal.core.model.ApprovalStatus;
import vn.com.mbbank.adminportal.core.model.TransferChannel;

import java.time.OffsetDateTime;

@Data
@Accessors(chain = true)
@Table(name = "PAP_WHITELIST_ACCOUNT")
@Entity
public class WhitelistAccount {
  @Id
  @SequenceGenerator(name = "PAP_WHITELIST_ACCOUNT_ID_SEQ", sequenceName = "PAP_WHITELIST_ACCOUNT_ID_SEQ", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PAP_WHITELIST_ACCOUNT_ID_SEQ")
  Long id;
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

  public WhitelistAccount shallowCopy() {
    return new WhitelistAccount()
        .setId(this.id)
        .setBankCode(this.bankCode)
        .setTransferChannel(this.transferChannel)
        .setAccountNo(this.accountNo)
        .setApprovalStatus(this.approvalStatus)
        .setActive(this.active)
        .setReason(this.reason)
        .setCreatedAt(this.createdAt)
        .setCreatedBy(this.createdBy)
        .setUpdatedAt(this.updatedAt)
        .setUpdatedBy(this.updatedBy)
        .setApprovedAt(this.approvedAt)
        .setApprovedBy(this.approvedBy);
  }
}
