package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.common.mapper.BeanMapper;
import vn.com.mbbank.adminportal.core.model.TransferChannel;
import vn.com.mbbank.adminportal.core.model.entity.TransferChannelLimit;
import vn.com.mbbank.adminportal.core.model.request.CreateTransferChannelLimitRequest;

import java.time.OffsetDateTime;

@Mapper(imports = {TransferChannel.class})
public interface CreateTransferChannelLimitRequest2TransferChannelLimitMapper extends BeanMapper<CreateTransferChannelLimitRequest, TransferChannelLimit> {
    CreateTransferChannelLimitRequest2TransferChannelLimitMapper INSTANCE = Mappers.getMapper(CreateTransferChannelLimitRequest2TransferChannelLimitMapper.class);

    TransferChannelLimit map(CreateTransferChannelLimitRequest request, String username);
    @AfterMapping
    default void afterMapping(CreateTransferChannelLimitRequest request, String username, @MappingTarget TransferChannelLimit target) {
        var now = OffsetDateTime.now();
        target.setCreatedBy(username);
        target.setCreatedAt(now);
        target.setUpdatedBy(username);
        target.setUpdatedAt(now);
    }
}
