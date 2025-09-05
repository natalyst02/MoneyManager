package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.core.model.ApprovalStatus;
import vn.com.mbbank.adminportal.core.model.entity.WhitelistAccount;
import vn.com.mbbank.adminportal.core.model.request.UpdateWhitelistAccountRequest;

import java.time.OffsetDateTime;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UpdateWhitelistAccountReq2WhitelistAccount {
  UpdateWhitelistAccountReq2WhitelistAccount INSTANCE = Mappers.getMapper(UpdateWhitelistAccountReq2WhitelistAccount.class);

  WhitelistAccount map(UpdateWhitelistAccountRequest updateRequest, @MappingTarget WhitelistAccount whitelistAccount, String username);

  @AfterMapping
  default void afterMapping(@MappingTarget WhitelistAccount target, String username) {
    target.setUpdatedBy(username)
        .setUpdatedAt(OffsetDateTime.now())
        .setApprovalStatus(ApprovalStatus.WAITING_APPROVAL);
  }
}
