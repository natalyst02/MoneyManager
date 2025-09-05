package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.common.mapper.BeanMapper;
import vn.com.mbbank.adminportal.core.model.entity.WhitelistAccount;
import vn.com.mbbank.adminportal.core.model.message.WhitelistAccountMessage;

@Mapper
public interface WhitelistAccount2MessageMapper extends BeanMapper<WhitelistAccount, WhitelistAccountMessage> {
  WhitelistAccount2MessageMapper INSTANCE = Mappers.getMapper(WhitelistAccount2MessageMapper.class);
}
