package vn.com.mbbank.adminportal.core.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.com.mbbank.adminportal.common.model.response.Response;
import vn.com.mbbank.adminportal.core.model.request.TransferChannelConfigsFilter;
import vn.com.mbbank.adminportal.core.model.request.UpdateTransferChannelConfigPrioritiesRequest;
import vn.com.mbbank.adminportal.core.model.request.UpdateTransferChannelConfigStatusRequest;
import vn.com.mbbank.adminportal.core.model.response.GetTransferChannelConfigResponse;
import vn.com.mbbank.adminportal.core.model.response.UpdateTransferChannelConfigPrioritiesResponse;
import vn.com.mbbank.adminportal.core.model.response.UpdateTransferChannelConfigStatusResponse;
import vn.com.mbbank.adminportal.core.service.TransferChannelConfigService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/transfer-channel-configs")
@RequiredArgsConstructor
@Validated
public class TransferChannelConfigController {
  private final TransferChannelConfigService transferChannelConfigService;

  @GetMapping
  public Response<List<GetTransferChannelConfigResponse>> getTransferChannelConfigs(Authentication authentication, TransferChannelConfigsFilter request) {
    return Response.ofSucceeded(transferChannelConfigService.getTransferChannelConfigs(authentication, request));
  }

  @PutMapping("/status")
  public CompletableFuture<Response<UpdateTransferChannelConfigStatusResponse>> updateStatus(Authentication authentication, @RequestBody @Valid UpdateTransferChannelConfigStatusRequest request) {
    return transferChannelConfigService.updateStatus(authentication, request).thenApply(Response::ofSucceeded);
  }

  @PutMapping("/priorities")
  public CompletableFuture<Response<UpdateTransferChannelConfigPrioritiesResponse>> updatePriorities(Authentication authentication, @RequestBody UpdateTransferChannelConfigPrioritiesRequest request) {
    return transferChannelConfigService.updatePriorities(authentication, request).thenApply(Response::ofSucceeded);
  }
}
