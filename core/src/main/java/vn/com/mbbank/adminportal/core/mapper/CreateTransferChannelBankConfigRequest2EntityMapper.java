package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.core.model.entity.TransferChannelBankConfig;
import vn.com.mbbank.adminportal.core.model.request.CreateTransferChannelBankConfigRequest;

import java.time.OffsetDateTime;

@Mapper
public interface CreateTransferChannelBankConfigRequest2EntityMapper {
  CreateTransferChannelBankConfigRequest2EntityMapper INSTANCE = Mappers.getMapper(CreateTransferChannelBankConfigRequest2EntityMapper.class);

  TransferChannelBankConfig map(CreateTransferChannelBankConfigRequest source, String username);

  @AfterMapping
  default void afterMapping(String username, @MappingTarget TransferChannelBankConfig target) {
    target.setActive(true)
        .setCreatedBy(username)
        .setCreatedAt(OffsetDateTime.now())
        .setUpdatedBy(username)
        .setUpdatedAt(target.getCreatedAt());
  }
}
