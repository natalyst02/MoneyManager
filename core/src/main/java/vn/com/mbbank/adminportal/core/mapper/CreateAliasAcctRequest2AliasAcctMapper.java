package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.common.mapper.BeanMapper;
import vn.com.mbbank.adminportal.core.model.entity.AliasAccount;
import vn.com.mbbank.adminportal.core.model.request.CreateAliasAccountRequest;
import vn.com.mbbank.adminportal.core.model.ApprovalStatus;

import java.time.OffsetDateTime;

@Mapper(imports = {ApprovalStatus.class})
public interface CreateAliasAcctRequest2AliasAcctMapper extends BeanMapper<CreateAliasAccountRequest, AliasAccount> {
  CreateAliasAcctRequest2AliasAcctMapper INSTANCE = Mappers.getMapper(CreateAliasAcctRequest2AliasAcctMapper.class);

  @Mapping(target = "approvalStatus", expression = "java(ApprovalStatus.WAITING_APPROVAL)")
  AliasAccount map(CreateAliasAccountRequest request, String username);

  @AfterMapping
  default void afterMapping(CreateAliasAccountRequest request, String username, @MappingTarget AliasAccount target) {
    target.setCreatedBy(username);
    target.setCreatedAt(OffsetDateTime.now());
    target.setUpdatedBy(username);
    target.setUpdatedAt(OffsetDateTime.now());
  }
}
