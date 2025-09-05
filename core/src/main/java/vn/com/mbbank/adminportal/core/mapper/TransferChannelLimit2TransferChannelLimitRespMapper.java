package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.common.mapper.BeanMapper;
import vn.com.mbbank.adminportal.core.model.entity.TransferChannelLimit;
import vn.com.mbbank.adminportal.core.model.response.TransferChannelLimitResp;

@Mapper
public interface TransferChannelLimit2TransferChannelLimitRespMapper extends BeanMapper<TransferChannelLimit, TransferChannelLimitResp> {
    TransferChannelLimit2TransferChannelLimitRespMapper INSTANCE = Mappers.getMapper(TransferChannelLimit2TransferChannelLimitRespMapper.class);
}
