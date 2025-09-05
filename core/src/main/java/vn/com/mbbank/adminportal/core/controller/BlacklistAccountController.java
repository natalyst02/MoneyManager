package vn.com.mbbank.adminportal.core.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.com.mbbank.adminportal.common.model.response.PageImpl;
import vn.com.mbbank.adminportal.common.model.response.Response;
import vn.com.mbbank.adminportal.common.util.Pageables;
import vn.com.mbbank.adminportal.core.model.filter.BlacklistAccountFilter;
import vn.com.mbbank.adminportal.core.model.filter.BlacklistAccountHistoryFilter;
import vn.com.mbbank.adminportal.core.model.request.CreateBlacklistAccountRequest;
import vn.com.mbbank.adminportal.core.model.request.UpdateBlacklistAccountRequest;
import vn.com.mbbank.adminportal.core.model.response.BlacklistAccountHistoryResponse;
import vn.com.mbbank.adminportal.core.model.response.BlacklistAccountResponse;
import vn.com.mbbank.adminportal.core.service.BlacklistAccountService;

import java.util.concurrent.CompletableFuture;

@Log4j2
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/blacklist-accounts")
public class BlacklistAccountController {
  private final BlacklistAccountService blacklistAccountService;

  @GetMapping
  public Response<PageImpl<BlacklistAccountResponse>> search(@Valid BlacklistAccountFilter request,
                                                             @RequestParam int page, @RequestParam int size,
                                                             @RequestParam(required = false, defaultValue = "id:ASC") String sort) {
    return Response.ofSucceeded(blacklistAccountService.search(request, Pageables.of(page, size, sort)));
  }

  @GetMapping("/{id}")
  public Response<BlacklistAccountResponse> get(@PathVariable @Positive Long id) {
    return Response.ofSucceeded(blacklistAccountService.get(id));
  }

  @PostMapping
  public CompletableFuture<Response<BlacklistAccountResponse>> create(Authentication authentication,
                                                                      @RequestBody @Valid CreateBlacklistAccountRequest createBlacklistAccountRequest) {
    return blacklistAccountService.create(authentication, createBlacklistAccountRequest).thenApply(Response::ofSucceeded);
  }

  @PutMapping("/{id}")
  public CompletableFuture<Response<BlacklistAccountResponse>> update(Authentication authentication,
                                                                      @PathVariable @Positive Long id,
                                                                      @RequestBody @Valid UpdateBlacklistAccountRequest updateBlacklistAccountRequest) {
    return blacklistAccountService.update(authentication, id, updateBlacklistAccountRequest).thenApply(Response::ofSucceeded);
  }

  @GetMapping("/{id}/history")
  public Response<PageImpl<BlacklistAccountHistoryResponse>> searchHistory(@PathVariable @Positive Long id,
                                                                           @Valid BlacklistAccountHistoryFilter request,
                                                                           @RequestParam int page, @RequestParam int size,
                                                                           @RequestParam(required = false, defaultValue = "id:ASC") String sort) {
    return Response.ofSucceeded(blacklistAccountService.searchHistory(id, request, Pageables.of(page, size, sort)));
  }

  @GetMapping("/history/{id}")
  public Response<BlacklistAccountHistoryResponse> getHistory(@PathVariable @Positive Long id) {
    return Response.ofSucceeded(blacklistAccountService.getHistory(id));
  }
}
