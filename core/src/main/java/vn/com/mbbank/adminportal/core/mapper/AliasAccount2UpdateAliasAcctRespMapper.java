package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.common.mapper.BeanMapper;
import vn.com.mbbank.adminportal.core.model.entity.AliasAccount;
import vn.com.mbbank.adminportal.core.model.response.UpdateAliasAccountResp;

@Mapper
public interface AliasAccount2UpdateAliasAcctRespMapper extends BeanMapper<AliasAccount, UpdateAliasAccountResp> {
  AliasAccount2UpdateAliasAcctRespMapper INSTANCE = Mappers.getMapper(AliasAccount2UpdateAliasAcctRespMapper.class);
}
