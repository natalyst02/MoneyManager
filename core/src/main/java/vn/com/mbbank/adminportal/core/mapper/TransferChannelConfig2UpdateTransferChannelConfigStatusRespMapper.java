package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.common.mapper.BeanMapper;
import vn.com.mbbank.adminportal.core.model.entity.TransferChannelConfig;
import vn.com.mbbank.adminportal.core.model.response.UpdateTransferChannelConfigStatusResponse;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransferChannelConfig2UpdateTransferChannelConfigStatusRespMapper extends BeanMapper<TransferChannelConfig, UpdateTransferChannelConfigStatusResponse> {
  TransferChannelConfig2UpdateTransferChannelConfigStatusRespMapper INSTANCE = Mappers.getMapper(TransferChannelConfig2UpdateTransferChannelConfigStatusRespMapper.class);
}
