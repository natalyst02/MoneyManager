package vn.com.mbbank.adminportal.core.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.com.mbbank.adminportal.common.model.response.PageImpl;
import vn.com.mbbank.adminportal.common.model.response.Response;
import vn.com.mbbank.adminportal.common.util.Pageables;
import vn.com.mbbank.adminportal.core.model.filter.AliasAccountFilter;
import vn.com.mbbank.adminportal.core.model.filter.AliasAccountHistoryFilter;
import vn.com.mbbank.adminportal.core.model.request.CreateAliasAccountRequest;
import vn.com.mbbank.adminportal.core.model.request.RejectAliasAccountRequest;
import vn.com.mbbank.adminportal.core.model.request.UpdateAliasAccountRequest;
import vn.com.mbbank.adminportal.core.model.response.AliasAccountHistoryResp;
import vn.com.mbbank.adminportal.core.model.response.AliasAccountResp;
import vn.com.mbbank.adminportal.core.model.response.ApproveAliasAccountResp;
import vn.com.mbbank.adminportal.core.model.response.CreateAliasAccountResp;
import vn.com.mbbank.adminportal.core.model.response.RejectAliasAccountResp;
import vn.com.mbbank.adminportal.core.model.response.UpdateAliasAccountResp;
import vn.com.mbbank.adminportal.core.service.AliasAccountService;


@RestController
@RequestMapping("/alias-accounts")
@RequiredArgsConstructor
@Validated
public class AliasAccountController {
  private final AliasAccountService aliasAccountService;

  @PostMapping
  public Response<CreateAliasAccountResp> create(Authentication authentication, @RequestBody @Valid CreateAliasAccountRequest request) {
    return Response.ofSucceeded(aliasAccountService.create(authentication, request));
  }

  @PutMapping("/{id}")
  public Response<UpdateAliasAccountResp> update(Authentication authentication,
                                                 @PathVariable @Positive Long id,
                                                 @RequestBody @Valid UpdateAliasAccountRequest request) {
    return Response.ofSucceeded(aliasAccountService.update(authentication, id, request));
  }

  @PostMapping("/{id}/reject")
  public Response<RejectAliasAccountResp> reject(Authentication authentication,
                                                 @PathVariable @Positive Long id,
                                                 @RequestBody @Valid RejectAliasAccountRequest request) {
    return Response.ofSucceeded(aliasAccountService.reject(authentication, id, request));
  }

  @PostMapping("/{id}/approve")
  public Response<ApproveAliasAccountResp> approve(Authentication authentication,
                                                         @PathVariable @Positive Long id) {
    return Response.ofSucceeded(aliasAccountService.approve(authentication, id));
  }

  @GetMapping
  public Response<PageImpl<AliasAccountResp>> getAccounts(@Valid AliasAccountFilter request, @RequestParam int page, @RequestParam int size,
                                                          @RequestParam(required = false, defaultValue = "id:ASC") String sort) {
    return Response.ofSucceeded(aliasAccountService.getAccounts(request, Pageables.of(page, size, sort)));
  }

  @GetMapping("/{id}")
  public Response<AliasAccountResp> getAccount(@PathVariable @Positive Long id) {
    return Response.ofSucceeded(aliasAccountService.getAccount(id));
  }

  @GetMapping("/{aliasAccountId}/history")
  public Response<PageImpl<AliasAccountHistoryResp>> getAccountHistory(@PathVariable @Positive Long aliasAccountId,
                                                                       @Valid AliasAccountHistoryFilter filter,
                                                                       @RequestParam int page, @RequestParam int size,
                                                                       @RequestParam(required = false, defaultValue = "id:ASC") String sort) {
    return Response.ofSucceeded(aliasAccountService.getAccountHistory(aliasAccountId, filter, Pageables.of(page, size, sort)));
  }

  @GetMapping("/history/{historyId}")
  public Response<AliasAccountHistoryResp> getAccountHistory(@PathVariable @Positive Long historyId) {
    return Response.ofSucceeded(aliasAccountService.getAccountHistory(historyId));
  }
}
