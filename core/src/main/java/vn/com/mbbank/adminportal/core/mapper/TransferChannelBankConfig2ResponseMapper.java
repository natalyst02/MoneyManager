package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.common.mapper.BeanMapper;
import vn.com.mbbank.adminportal.core.model.entity.TransferChannelBankConfig;
import vn.com.mbbank.adminportal.core.model.response.TransferChannelBankConfigResponse;

@Mapper
public interface TransferChannelBankConfig2ResponseMapper extends BeanMapper<TransferChannelBankConfig, TransferChannelBankConfigResponse> {
  TransferChannelBankConfig2ResponseMapper INSTANCE = Mappers.getMapper(TransferChannelBankConfig2ResponseMapper.class);
}
