package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.core.model.entity.TransferChannelBankConfig;
import vn.com.mbbank.adminportal.core.model.request.UpdateTransferChannelBankConfigRequest;

import java.time.OffsetDateTime;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, imports = {OffsetDateTime.class})
public interface UpdateTransferChannelBankConfigRequest2EntityMapper {
  UpdateTransferChannelBankConfigRequest2EntityMapper INSTANCE = Mappers.getMapper(UpdateTransferChannelBankConfigRequest2EntityMapper.class);

  @Mapping(target = "updatedBy", expression = "java(username)")
  @Mapping(target = "updatedAt", expression = "java(OffsetDateTime.now())")
  TransferChannelBankConfig map(UpdateTransferChannelBankConfigRequest source, String username, @MappingTarget TransferChannelBankConfig target);
}
