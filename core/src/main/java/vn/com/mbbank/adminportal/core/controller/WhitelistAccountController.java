package vn.com.mbbank.adminportal.core.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import vn.com.mbbank.adminportal.common.model.response.PageImpl;
import vn.com.mbbank.adminportal.common.model.response.Response;
import vn.com.mbbank.adminportal.common.util.Pageables;
import vn.com.mbbank.adminportal.core.model.filter.WhitelistAccountFilter;
import vn.com.mbbank.adminportal.core.model.filter.WhitelistAccountHistoryFilter;
import vn.com.mbbank.adminportal.core.model.request.CreateWhitelistAccountRequest;
import vn.com.mbbank.adminportal.core.model.request.RejectWhitelistAccountRequest;
import vn.com.mbbank.adminportal.core.model.request.UpdateWhitelistAccountRequest;
import vn.com.mbbank.adminportal.core.model.response.WhitelistAccountHistoryResponse;
import vn.com.mbbank.adminportal.core.model.response.WhitelistAccountResponse;
import vn.com.mbbank.adminportal.core.service.WhitelistAccountService;

@Log4j2
@RestController
@RequestMapping("/whitelist-accounts")
@RequiredArgsConstructor
@Validated
public class WhitelistAccountController {
  private final WhitelistAccountService whitelistAccountService;

  @PostMapping
  public Response<WhitelistAccountResponse> create(Authentication authentication,
                                                                      @RequestBody @Valid CreateWhitelistAccountRequest createWhitelistAccountRequest) {
    return Response.ofSucceeded(whitelistAccountService.create(authentication, createWhitelistAccountRequest));
  }

  @PutMapping("/{id}")
  public Response<WhitelistAccountResponse> update(Authentication authentication,
                                                                      @PathVariable @Positive Long id,
                                                                      @RequestBody @Valid UpdateWhitelistAccountRequest updateWhitelistAccountRequest) {
    return Response.ofSucceeded(whitelistAccountService.update(authentication, id, updateWhitelistAccountRequest));
  }

  @PostMapping("/{id}/reject")
  public Response<WhitelistAccountResponse> reject(Authentication authentication,
                                                                      @PathVariable @Positive Long id,
                                                                      @RequestBody @Valid RejectWhitelistAccountRequest rejectWhitelistAccountRequest) {
    return Response.ofSucceeded(whitelistAccountService.reject(authentication, id, rejectWhitelistAccountRequest));
  }

  @PostMapping("/{id}/approve")
  public Response<WhitelistAccountResponse> approve(Authentication authentication,
                                                    @PathVariable @Positive Long id) {
    return Response.ofSucceeded(whitelistAccountService.approve(authentication, id));
  }

  @GetMapping
  public Response<PageImpl<WhitelistAccountResponse>> getWhitelistAccounts(Authentication authentication,
                                                                           @Valid WhitelistAccountFilter request,
                                                                           @RequestParam int page, @RequestParam int size,
                                                                           @RequestParam(required = false, defaultValue = "id:ASC") String sort) {
    return Response.ofSucceeded(whitelistAccountService.getWhitelistAccounts(authentication, request, Pageables.of(page, size, sort)));
  }

  @GetMapping("/{id}")
  public Response<WhitelistAccountResponse> get(@PathVariable @Positive Long id) {
    return Response.ofSucceeded(whitelistAccountService.get(id));
  }

  @GetMapping("/{id}/history")
  public Response<PageImpl<WhitelistAccountHistoryResponse>> searchHistory(@PathVariable Long id, Authentication authentication,
                                                                           @Valid WhitelistAccountHistoryFilter request,
                                                                           @RequestParam int page, @RequestParam int size,
                                                                           @RequestParam(required = false, defaultValue = "id:ASC") String sort) {
    return Response.ofSucceeded(whitelistAccountService.searchHistory(id, authentication, request, Pageables.of(page, size, sort)));
  }

  @GetMapping("/history/{id}")
  public Response<WhitelistAccountHistoryResponse> getHistory(@PathVariable @Positive Long id) {
    return Response.ofSucceeded(whitelistAccountService.getHistory(id));
  }
}
