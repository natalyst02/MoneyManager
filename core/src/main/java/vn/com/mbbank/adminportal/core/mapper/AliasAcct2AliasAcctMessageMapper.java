package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.common.mapper.BeanMapper;
import vn.com.mbbank.adminportal.core.model.message.AliasAccountMessage;
import vn.com.mbbank.adminportal.core.model.entity.AliasAccount;

@Mapper
public interface AliasAcct2AliasAcctMessageMapper extends BeanMapper<AliasAccount, AliasAccountMessage> {
  AliasAcct2AliasAcctMessageMapper INSTANCE = Mappers.getMapper(AliasAcct2AliasAcctMessageMapper.class);
}
