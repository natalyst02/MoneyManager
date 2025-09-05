package vn.com.mbbank.adminportal.core.repository;

import vn.com.mbbank.adminportal.core.model.entity.TransferChannelConfig;
import vn.com.mbbank.adminportal.core.model.request.UpdateTransferChannelConfigPrioritiesRequest;
import vn.com.mbbank.adminportal.core.model.request.UpdateTransferChannelConfigStatusRequest;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface CustomizedTransferChannelConfigRepository {
  int[] updatePriorities(UpdateTransferChannelConfigPrioritiesRequest request, String updatedBy, OffsetDateTime updatedAt);

  Optional<TransferChannelConfig> updateStatus(UpdateTransferChannelConfigStatusRequest request, String updatedBy, OffsetDateTime updatedAt);
}