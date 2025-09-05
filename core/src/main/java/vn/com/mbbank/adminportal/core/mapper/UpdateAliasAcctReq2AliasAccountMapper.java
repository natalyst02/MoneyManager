package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.common.mapper.BeanMapper;
import vn.com.mbbank.adminportal.core.model.entity.AliasAccount;
import vn.com.mbbank.adminportal.core.model.request.UpdateAliasAccountRequest;

@Mapper
public interface UpdateAliasAcctReq2AliasAccountMapper extends BeanMapper<UpdateAliasAccountRequest, AliasAccount> {
  UpdateAliasAcctReq2AliasAccountMapper INSTANCE = Mappers.getMapper(UpdateAliasAcctReq2AliasAccountMapper.class);
}
