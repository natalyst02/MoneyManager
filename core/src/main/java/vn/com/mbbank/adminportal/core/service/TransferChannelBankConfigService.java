package vn.com.mbbank.adminportal.core.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import vn.com.mbbank.adminportal.core.model.filter.TransferChannelBankConfigFilter;
import vn.com.mbbank.adminportal.core.model.filter.TransferChannelBankConfigHistoryFilter;
import vn.com.mbbank.adminportal.core.model.request.CreateTransferChannelBankConfigRequest;
import vn.com.mbbank.adminportal.core.model.request.UpdateTransferChannelBankConfigRequest;
import vn.com.mbbank.adminportal.core.model.response.TransferChannelBankConfigHistoryResponse;
import vn.com.mbbank.adminportal.core.model.response.TransferChannelBankConfigResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface TransferChannelBankConfigService {
  TransferChannelBankConfigResponse getTransferChannelBankConfig(Authentication authentication, Long id);

  Page<TransferChannelBankConfigResponse> getTransferChannelBankConfigs(Authentication authentication, TransferChannelBankConfigFilter filter);

  CompletableFuture<List<TransferChannelBankConfigResponse>> create(Authentication authentication, CreateTransferChannelBankConfigRequest request);

  CompletableFuture<TransferChannelBankConfigResponse> update(Authentication authentication, UpdateTransferChannelBankConfigRequest request);

  TransferChannelBankConfigHistoryResponse getTransferChannelBankHistory(Authentication authentication, Long historyId);

  Page<TransferChannelBankConfigHistoryResponse> getTransferChannelBankHistory(Authentication authentication, TransferChannelBankConfigHistoryFilter filter);
}
