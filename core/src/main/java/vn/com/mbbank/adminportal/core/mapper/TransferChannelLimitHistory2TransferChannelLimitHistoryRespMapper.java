package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.common.mapper.BeanMapper;
import vn.com.mbbank.adminportal.core.model.entity.TransferChannelLimitHistory;
import vn.com.mbbank.adminportal.core.model.response.TransferChannelLimitHistoryResp;
@Mapper
public interface TransferChannelLimitHistory2TransferChannelLimitHistoryRespMapper extends BeanMapper<TransferChannelLimitHistory, TransferChannelLimitHistoryResp> {
    TransferChannelLimitHistory2TransferChannelLimitHistoryRespMapper INSTANCE = Mappers.getMapper(TransferChannelLimitHistory2TransferChannelLimitHistoryRespMapper.class);
}
