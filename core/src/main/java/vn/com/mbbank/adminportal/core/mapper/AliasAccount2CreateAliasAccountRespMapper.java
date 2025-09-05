package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.common.mapper.BeanMapper;
import vn.com.mbbank.adminportal.core.model.entity.AliasAccount;
import vn.com.mbbank.adminportal.core.model.response.CreateAliasAccountResp;

@Mapper
public interface AliasAccount2CreateAliasAccountRespMapper extends BeanMapper<AliasAccount, CreateAliasAccountResp> {
  AliasAccount2CreateAliasAccountRespMapper INSTANCE = Mappers.getMapper(AliasAccount2CreateAliasAccountRespMapper.class);
}
