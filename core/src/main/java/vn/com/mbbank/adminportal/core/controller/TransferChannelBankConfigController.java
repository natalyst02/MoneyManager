package vn.com.mbbank.adminportal.core.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.com.mbbank.adminportal.common.model.response.PageImpl;
import vn.com.mbbank.adminportal.common.model.response.Response;
import vn.com.mbbank.adminportal.core.model.filter.TransferChannelBankConfigFilter;
import vn.com.mbbank.adminportal.core.model.filter.TransferChannelBankConfigHistoryFilter;
import vn.com.mbbank.adminportal.core.model.request.CreateTransferChannelBankConfigRequest;
import vn.com.mbbank.adminportal.core.model.request.UpdateTransferChannelBankConfigRequest;
import vn.com.mbbank.adminportal.core.model.response.TransferChannelBankConfigHistoryResponse;
import vn.com.mbbank.adminportal.core.model.response.TransferChannelBankConfigResponse;
import vn.com.mbbank.adminportal.core.service.TransferChannelBankConfigService;

import java.util.List;
import java.util.concurrent.CompletableFuture;


@RequiredArgsConstructor
@RestController
@RequestMapping("/transfer-channel-bank-configs")
@Validated
public class TransferChannelBankConfigController {
  private final TransferChannelBankConfigService transferChannelBankConfigService;

  @GetMapping
  public Response<PageImpl<TransferChannelBankConfigResponse>> getTransferChannelBankConfigs(Authentication authentication, @Valid TransferChannelBankConfigFilter filter) {
    return Response.ofSucceeded(transferChannelBankConfigService.getTransferChannelBankConfigs(authentication, filter));
  }

  @PostMapping
  public CompletableFuture<Response<List<TransferChannelBankConfigResponse>>> create(Authentication authentication, @Valid @RequestBody CreateTransferChannelBankConfigRequest request) {
    return transferChannelBankConfigService.create(authentication, request).thenApply(Response::ofSucceeded);
  }

  @PutMapping("/{id}")
  public CompletableFuture<Response<TransferChannelBankConfigResponse>> update(Authentication authentication, @Positive @PathVariable Long id, @Valid @RequestBody UpdateTransferChannelBankConfigRequest request) {
    return transferChannelBankConfigService.update(authentication, request.setId(id)).thenApply(Response::ofSucceeded);
  }

  @GetMapping("/history/{historyId}")
  public Response<TransferChannelBankConfigHistoryResponse> getTransferChannelBankConfigHistory(Authentication authentication, @PathVariable @Positive Long historyId) {
    return Response.ofSucceeded(transferChannelBankConfigService.getTransferChannelBankHistory(authentication, historyId));
  }

  @GetMapping("/{transferChannelBankConfigId}/history")
  public Response<PageImpl<TransferChannelBankConfigHistoryResponse>> getTransferChannelBankConfigHistory(Authentication authentication, @PathVariable @Positive Long transferChannelBankConfigId,
                                                                                                          @Valid TransferChannelBankConfigHistoryFilter filter) {
    filter.setTransferChannelBankConfigId(transferChannelBankConfigId);
    return Response.ofSucceeded(transferChannelBankConfigService.getTransferChannelBankHistory(authentication, filter));
  }
}
