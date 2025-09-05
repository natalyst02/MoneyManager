package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.common.mapper.BeanMapper;
import vn.com.mbbank.adminportal.core.model.entity.AliasAccount;
import vn.com.mbbank.adminportal.core.model.response.RejectAliasAccountResp;

@Mapper
public interface AliasAccount2RejectAliasAcctRespMapper extends BeanMapper<AliasAccount, RejectAliasAccountResp> {
  AliasAccount2RejectAliasAcctRespMapper INSTANCE = Mappers.getMapper(AliasAccount2RejectAliasAcctRespMapper.class);
}
