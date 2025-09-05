package vn.com.mbbank.adminportal.core.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.common.mapper.BeanMapper;
import vn.com.mbbank.adminportal.core.model.entity.TransferChannelBankConfig;
import vn.com.mbbank.adminportal.core.model.message.TransferChannelBankConfigMessage;

@Mapper
public interface TransferChannelBankConfig2MessageMapper extends BeanMapper<TransferChannelBankConfig, TransferChannelBankConfigMessage> {
  TransferChannelBankConfig2MessageMapper INSTANCE = Mappers.getMapper(TransferChannelBankConfig2MessageMapper.class);
}
