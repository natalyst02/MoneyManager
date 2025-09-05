package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.common.mapper.BeanMapper;
import vn.com.mbbank.adminportal.core.model.entity.TransferChannelLimit;
import vn.com.mbbank.adminportal.core.model.message.TransferChannelLimitMessage;

@Mapper
public interface TransferChannelLimit2TransferChannelLimitMessageMapper extends BeanMapper<TransferChannelLimit, TransferChannelLimitMessage> {
    TransferChannelLimit2TransferChannelLimitMessageMapper INSTANCE = Mappers.getMapper(TransferChannelLimit2TransferChannelLimitMessageMapper.class);
}
