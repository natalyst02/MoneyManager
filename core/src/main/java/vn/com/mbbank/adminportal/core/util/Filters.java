package vn.com.mbbank.adminportal.core.util;

import org.springframework.data.jpa.domain.Specification;
import vn.com.mbbank.adminportal.common.util.Specifications;
import vn.com.mbbank.adminportal.core.model.TransferChannel;
import vn.com.mbbank.adminportal.core.model.entity.*;
import vn.com.mbbank.adminportal.core.model.filter.*;
import vn.com.mbbank.adminportal.core.model.request.TransferChannelConfigsFilter;

import java.util.Collections;
import java.util.List;

public final class Filters {
  private Filters() {
    throw new UnsupportedOperationException();
  }

  public static Specification<Role> toSpecification(RoleFilter filter) {
    return Specification.where(Specifications.contain(Role_.code, filter.getCode()))
        .and(Specifications.contain(Role_.name, filter.getName()))
        .and(Specifications.in(Role_.type, filter.getType()))
        .and(Specifications.equals(Role_.active, filter.getActive()));
  }

  public static Specification<AliasAccount> toSpecification(AliasAccountFilter filter) {
    return Specification.where(Specifications.contain(AliasAccount_.name, filter.getName()))
        .and(Specifications.equals(AliasAccount_.active, filter.getActive()))
        .and(Specifications.equals(AliasAccount_.approvalStatus, filter.getApprovalStatus()));

  }
  public static Specification<Bank> toSpecification(BankFilter filter) {
    return Specification.where(Specifications.equals(Bank_.code, filter.getCode()))
        .and(Specifications.equals(Bank_.active, filter.getActive()))
        .and(Specifications.equals(Bank_.shortName, filter.getShortName()))
        .and(Specifications.equals(Bank_.fullName, filter.getFullName()));
  }


  public static Specification<WhitelistAccount> toSpecification(WhitelistAccountFilter filter) {
    return Specification.where(Specifications.contain(WhitelistAccount_.accountNo, filter.getAccountNo()))
        .and(Specifications.contain(WhitelistAccount_.bankCode, filter.getBankCode()))
        .and(Specifications.equals(WhitelistAccount_.transferChannel, filter.getTransferChannel()))
        .and(Specifications.equals(WhitelistAccount_.active, filter.getActive()))
        .and(Specifications.equals(WhitelistAccount_.approvalStatus, filter.getApprovalStatus()));
  }

  public static Specification<WhitelistAccountHistory> toSpecification(WhitelistAccountHistoryFilter filter) {
    return Specification.where(Specifications.equals(WhitelistAccountHistory_.whitelistAccountId, filter.getWhitelistAccountId()))
        .and(Specifications.contain(WhitelistAccountHistory_.updatedBy, filter.getUpdatedBy()))
        .and(Specifications.greaterThanOrEqualTo(WhitelistAccountHistory_.updatedAt, filter.getUpdatedAtFrom()))
        .and(Specifications.lessThanOrEqualTo(WhitelistAccountHistory_.updatedAt, filter.getUpdatedAtTo()));
  }

  public static Specification<AliasAccountHistory> toSpecification(AliasAccountHistoryFilter filter) {
    return Specification.where(Specifications.equals(AliasAccountHistory_.papAliasAccountId, filter.getAliasAccountId()))
        .and(Specifications.contain(AliasAccountHistory_.updatedBy, filter.getUpdatedBy()))
        .and(Specifications.greaterThanOrEqualTo(AliasAccountHistory_.updatedAt, filter.getUpdatedAtFrom()))
        .and(Specifications.lessThanOrEqualTo(AliasAccountHistory_.updatedAt, filter.getUpdatedAtTo()));

  }
  public static Specification<TransferChannelLimit> toSpecification(TransferChannelLimitFilter filter) {
    return Specification.where(Specifications.equals(TransferChannelLimit_.transferChannel, filter.getTransferChannel()))
            .and(Specifications.equals(TransferChannelLimit_.active, filter.getActive()));
  }
  public static Specification<TransferChannelLimitHistory> toSpecification(TransferChannelLimitHistoryFilter filter) {
    return Specification.where(Specifications.equals(TransferChannelLimitHistory_.transferChannelId, filter.getTransferChannelId()))
            .and(Specifications.contain(TransferChannelLimitHistory_.updatedBy, filter.getUpdatedBy()))
            .and(Specifications.greaterThanOrEqualTo(TransferChannelLimitHistory_.updatedAt, filter.getUpdatedAtFrom()))
            .and(Specifications.lessThanOrEqualTo(TransferChannelLimitHistory_.updatedAt, filter.getUpdatedAtTo()));
  }

  public static Specification<TransferChannelConfig> toSpecification(TransferChannelConfigsFilter filter) {
    var transferChannel = Collections.EMPTY_LIST;
    if (filter.getTransferChannel() != null) {
      transferChannel = List.of(filter.getTransferChannel());
    }
    if (filter.getTransferType() != null && filter.getTransferChannel() == null) {
      transferChannel = TransferChannel.getByTransferType(filter.getTransferType());
    }
    return Specification.where(Specifications.in(TransferChannelConfig_.transferChannel, transferChannel));
  }

  public static Specification<TransferChannelBankConfig> toSpecification(TransferChannelBankConfigFilter filter) {
    return Specification.where(Specifications.equals(TransferChannelBankConfig_.bankCode, filter.getBankCode()))
        .and(Specifications.contain(TransferChannelBankConfig_.cardBin, filter.getCardBin()))
        .and(Specifications.equals(TransferChannelBankConfig_.transferChannel, filter.getTransferChannel()))
        .and(Specifications.equals(TransferChannelBankConfig_.active, filter.getActive()))
        ;
  }

  public static Specification<TransferChannelBankConfigHistory> toSpecification(TransferChannelBankConfigHistoryFilter filter) {
    return Specification.where(Specifications.equals(TransferChannelBankConfigHistory_.transferChannelBankConfigId, filter.getTransferChannelBankConfigId()))
        .and(Specifications.contain(TransferChannelBankConfigHistory_.updatedBy, filter.getUpdatedBy()))
        .and(Specifications.greaterThanOrEqualTo(TransferChannelBankConfigHistory_.updatedAt, filter.getUpdatedAtFrom()))
        .and(Specifications.lessThanOrEqualTo(TransferChannelBankConfigHistory_.updatedAt, filter.getUpdatedAtTo()));
  }

  public static Specification<BlacklistAccount> toSpecification(BlacklistAccountFilter filter) {
    return Specification.where(Specifications.contain(BlacklistAccount_.accountNo, filter.getAccountNo()))
        .and(Specifications.contain(BlacklistAccount_.bankCode, filter.getBankCode()))
        .and(Specifications.equals(BlacklistAccount_.type, filter.getType()))
        .and(Specifications.equals(BlacklistAccount_.transactionType, filter.getTransactionType()))
        .and(Specifications.equals(BlacklistAccount_.active, filter.getActive()));
  }

  public static Specification<BlacklistAccountHistory> toSpecification(BlacklistAccountHistoryFilter filter) {
    return Specification.where(Specifications.equals(BlacklistAccountHistory_.blacklistAccountId, filter.getBlacklistAccountId()))
        .and(Specifications.contain(BlacklistAccountHistory_.updatedBy, filter.getUpdatedBy()))
        .and(Specifications.greaterThanOrEqualTo(BlacklistAccountHistory_.updatedAt, filter.getUpdatedAtFrom()))
        .and(Specifications.lessThanOrEqualTo(BlacklistAccountHistory_.updatedAt, filter.getUpdatedAtTo()));
  }
}
