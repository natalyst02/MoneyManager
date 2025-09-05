package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.common.mapper.BeanMapper;
import vn.com.mbbank.adminportal.core.model.entity.AliasAccount;
import vn.com.mbbank.adminportal.core.model.response.ApproveAliasAccountResp;

@Mapper
public interface AliasAccount2ApproveAliasAccountRespMapper extends BeanMapper<AliasAccount, ApproveAliasAccountResp> {
  AliasAccount2ApproveAliasAccountRespMapper INSTANCE = Mappers.getMapper(AliasAccount2ApproveAliasAccountRespMapper.class);
}
