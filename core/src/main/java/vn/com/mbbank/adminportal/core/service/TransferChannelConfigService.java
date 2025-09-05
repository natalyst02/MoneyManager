package vn.com.mbbank.adminportal.core.service;

import org.springframework.security.core.Authentication;
import vn.com.mbbank.adminportal.core.model.request.TransferChannelConfigsFilter;
import vn.com.mbbank.adminportal.core.model.request.UpdateTransferChannelConfigPrioritiesRequest;
import vn.com.mbbank.adminportal.core.model.request.UpdateTransferChannelConfigStatusRequest;
import vn.com.mbbank.adminportal.core.model.response.GetTransferChannelConfigResponse;
import vn.com.mbbank.adminportal.core.model.response.UpdateTransferChannelConfigPrioritiesResponse;
import vn.com.mbbank.adminportal.core.model.response.UpdateTransferChannelConfigStatusResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface TransferChannelConfigService {
  List<GetTransferChannelConfigResponse> getTransferChannelConfigs(Authentication authentication, TransferChannelConfigsFilter request);

  CompletableFuture<UpdateTransferChannelConfigStatusResponse> updateStatus(Authentication authentication, UpdateTransferChannelConfigStatusRequest request);

  CompletableFuture<UpdateTransferChannelConfigPrioritiesResponse> updatePriorities(Authentication authentication, UpdateTransferChannelConfigPrioritiesRequest request);
}
