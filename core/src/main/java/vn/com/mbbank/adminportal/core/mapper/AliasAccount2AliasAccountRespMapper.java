package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.common.mapper.BeanMapper;
import vn.com.mbbank.adminportal.core.model.entity.AliasAccount;
import vn.com.mbbank.adminportal.core.model.response.AliasAccountResp;

@Mapper
public interface AliasAccount2AliasAccountRespMapper extends BeanMapper<AliasAccount, AliasAccountResp> {
  AliasAccount2AliasAccountRespMapper INSTANCE = Mappers.getMapper(AliasAccount2AliasAccountRespMapper.class);
}
