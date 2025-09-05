package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.common.mapper.BeanMapper;
import vn.com.mbbank.adminportal.core.model.entity.TransferChannelBankConfigHistory;
import vn.com.mbbank.adminportal.core.model.response.TransferChannelBankConfigHistoryResponse;

@Mapper
public interface TransferChannelBankConfigHistory2ResponseMapper extends BeanMapper<TransferChannelBankConfigHistory, TransferChannelBankConfigHistoryResponse> {
  TransferChannelBankConfigHistory2ResponseMapper INSTANCE = Mappers.getMapper(TransferChannelBankConfigHistory2ResponseMapper.class);
}
