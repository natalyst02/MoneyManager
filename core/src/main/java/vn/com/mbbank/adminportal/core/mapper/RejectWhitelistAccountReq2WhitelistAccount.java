package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.core.model.ApprovalStatus;
import vn.com.mbbank.adminportal.core.model.entity.WhitelistAccount;
import vn.com.mbbank.adminportal.core.model.request.RejectWhitelistAccountRequest;

import java.time.OffsetDateTime;

@Mapper(imports = {OffsetDateTime.class}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RejectWhitelistAccountReq2WhitelistAccount {
  RejectWhitelistAccountReq2WhitelistAccount INSTANCE = Mappers.getMapper(RejectWhitelistAccountReq2WhitelistAccount.class);

  WhitelistAccount map(RejectWhitelistAccountRequest updateRequest, @MappingTarget WhitelistAccount whitelistAccount, String username);

  @AfterMapping
  default void afterMapping(@MappingTarget WhitelistAccount target, String username) {
    target.setUpdatedBy(username)
        .setUpdatedAt(OffsetDateTime.now())
        .setApprovalStatus(ApprovalStatus.REJECTED);
  }
}
