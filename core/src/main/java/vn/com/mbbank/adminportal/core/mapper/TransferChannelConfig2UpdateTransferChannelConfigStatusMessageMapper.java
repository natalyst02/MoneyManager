package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.common.mapper.BeanMapper;
import vn.com.mbbank.adminportal.core.model.entity.TransferChannelConfig;
import vn.com.mbbank.adminportal.core.model.message.UpdateTransferChannelConfigStatusMessage;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransferChannelConfig2UpdateTransferChannelConfigStatusMessageMapper extends BeanMapper<TransferChannelConfig, UpdateTransferChannelConfigStatusMessage> {
  TransferChannelConfig2UpdateTransferChannelConfigStatusMessageMapper INSTANCE = Mappers.getMapper(TransferChannelConfig2UpdateTransferChannelConfigStatusMessageMapper.class);
}
