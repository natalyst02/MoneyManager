package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.common.mapper.BeanMapper;
import vn.com.mbbank.adminportal.core.model.entity.BlacklistAccount;
import vn.com.mbbank.adminportal.core.model.message.BlacklistAccountMessage;

@Mapper
public interface BlacklistAccount2MessageMapper extends BeanMapper<BlacklistAccount, BlacklistAccountMessage> {
  BlacklistAccount2MessageMapper INSTANCE = Mappers.getMapper(BlacklistAccount2MessageMapper.class);
}
