package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.common.mapper.BeanMapper;
import vn.com.mbbank.adminportal.core.model.entity.TransferChannelLimit;
import vn.com.mbbank.adminportal.core.model.request.UpdateTransferChannelLimitRequest;

@Mapper
public interface UpdateTransferChannelLimitReq2TransferChannelLimitMapper extends BeanMapper<UpdateTransferChannelLimitRequest, TransferChannelLimit> {
    UpdateTransferChannelLimitReq2TransferChannelLimitMapper INSTANCE = Mappers.getMapper(UpdateTransferChannelLimitReq2TransferChannelLimitMapper.class);
}
