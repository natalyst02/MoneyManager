package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.common.exception.PaymentPlatformException;
import vn.com.mbbank.adminportal.common.mapper.BeanMapper;
import vn.com.mbbank.adminportal.core.model.TransferChannelPriority;
import vn.com.mbbank.adminportal.core.model.TransferChannelReference;
import vn.com.mbbank.adminportal.core.model.message.UpdateTransferChannelConfigPrioritiesMessage;
import vn.com.mbbank.adminportal.core.model.request.UpdateTransferChannelConfigPrioritiesRequest;
import vn.com.mbbank.adminportal.core.util.ErrorCode;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UpdateTransferChannelConfigPrioritiesReq2UpdateTransferChannelPrioritiesMessageMapper extends BeanMapper<UpdateTransferChannelConfigPrioritiesRequest, UpdateTransferChannelConfigPrioritiesMessage> {
  UpdateTransferChannelConfigPrioritiesReq2UpdateTransferChannelPrioritiesMessageMapper INSTANCE = Mappers.getMapper(UpdateTransferChannelConfigPrioritiesReq2UpdateTransferChannelPrioritiesMessageMapper.class);

  UpdateTransferChannelConfigPrioritiesMessage map(UpdateTransferChannelConfigPrioritiesRequest source, List<TransferChannelReference> transferChannelReferences, String updatedBy, OffsetDateTime updatedAt);

  @AfterMapping
  default void afterMapping(@MappingTarget UpdateTransferChannelConfigPrioritiesMessage target, UpdateTransferChannelConfigPrioritiesRequest source, List<TransferChannelReference> transferChannelReferences) {
    var transferChannelPriorities = source.getPriorities()
        .stream()
        .map(priorityReference -> {
          var transferChannel = transferChannelReferences.stream()
              .filter(transferChannelReference -> Objects.equals(transferChannelReference.getId(), priorityReference.getId()))
              .map(TransferChannelReference::getTransferChannel).findAny()
              .orElseThrow(() -> new PaymentPlatformException(ErrorCode.TRANSFER_CHANNEL_CONFIG_NOT_FOUND, "Transfer channel config not found with id: " + priorityReference.getId()));
          return new TransferChannelPriority().setPriority(priorityReference.getPriority()).setTransferChannel(transferChannel);
        })
        .toList();
    target.setTransferChannelPriorities(transferChannelPriorities);
  }
}
