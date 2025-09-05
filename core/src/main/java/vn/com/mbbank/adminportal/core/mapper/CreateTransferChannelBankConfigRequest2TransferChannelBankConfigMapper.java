package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.core.model.TransferChannelStatus;
import vn.com.mbbank.adminportal.core.model.entity.TransferChannelBankConfig;
import vn.com.mbbank.adminportal.core.model.request.CreateTransferChannelBankConfigRequest;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface CreateTransferChannelBankConfigRequest2TransferChannelBankConfigMapper {
    CreateTransferChannelBankConfigRequest2TransferChannelBankConfigMapper INSTANCE = Mappers.getMapper(CreateTransferChannelBankConfigRequest2TransferChannelBankConfigMapper.class);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "transferChannel", source = "request.transferChannel")
    @Mapping(target = "active", source = "request.active")
    @Mapping(target = "cardBin", source = "cardBin")
    @Mapping(target = "bankCode", source = "bankCode")
    @Mapping(target = "reason", source = "reason")
    TransferChannelBankConfig map(TransferChannelStatus request,String bankCode, String cardBin, String reason, String username, OffsetDateTime now);

    @AfterMapping
    default void afterMapping(@MappingTarget TransferChannelBankConfig target, String username, OffsetDateTime now) {
        target.setCreatedBy(username);
        target.setCreatedAt(now);
        target.setUpdatedBy(username);
        target.setUpdatedAt(now);
    }

    default List<TransferChannelBankConfig> toEntityList(CreateTransferChannelBankConfigRequest request, String username) {
        var now = OffsetDateTime.now();
        return request.getTransferChannels().stream()
                .map(channelStatus -> map(channelStatus, request.getBankCode(), request.getCardBin(), request.getReason(), username, now))
                .collect(Collectors.toList());
    }

}
