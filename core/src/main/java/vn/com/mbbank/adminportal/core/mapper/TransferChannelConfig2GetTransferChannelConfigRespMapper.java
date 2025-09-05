package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.common.mapper.BeanMapper;
import vn.com.mbbank.adminportal.core.model.entity.TransferChannelConfig;
import vn.com.mbbank.adminportal.core.model.response.GetTransferChannelConfigResponse;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransferChannelConfig2GetTransferChannelConfigRespMapper extends BeanMapper<TransferChannelConfig, GetTransferChannelConfigResponse> {
  TransferChannelConfig2GetTransferChannelConfigRespMapper INSTANCE = Mappers.getMapper(TransferChannelConfig2GetTransferChannelConfigRespMapper.class);

  @Mapping(target = "transferType", source = "transferChannelConfig.transferChannel.transferType")
  GetTransferChannelConfigResponse map(TransferChannelConfig transferChannelConfig);
}
