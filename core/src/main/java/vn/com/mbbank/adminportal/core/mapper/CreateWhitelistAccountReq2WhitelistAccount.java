package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.common.mapper.BeanMapper;
import vn.com.mbbank.adminportal.core.model.ApprovalStatus;
import vn.com.mbbank.adminportal.core.model.entity.WhitelistAccount;
import vn.com.mbbank.adminportal.core.model.request.CreateWhitelistAccountRequest;

import java.time.OffsetDateTime;

@Mapper
public interface CreateWhitelistAccountReq2WhitelistAccount extends BeanMapper<CreateWhitelistAccountRequest, WhitelistAccount> {
  CreateWhitelistAccountReq2WhitelistAccount INSTANCE = Mappers.getMapper(CreateWhitelistAccountReq2WhitelistAccount.class);

  WhitelistAccount map(CreateWhitelistAccountRequest createWhitelistAccountRequest, String username);

  @AfterMapping
  default void afterMapping(String username, @MappingTarget WhitelistAccount target) {
    target.setCreatedBy(username)
        .setCreatedAt(OffsetDateTime.now())
        .setUpdatedBy(username)
        .setUpdatedAt(OffsetDateTime.now())
        .setApprovalStatus(ApprovalStatus.WAITING_APPROVAL)
        .setApprovedAt(null)
        .setApprovedBy(null);
  }
}
