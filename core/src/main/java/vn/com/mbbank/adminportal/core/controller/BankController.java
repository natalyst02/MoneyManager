package vn.com.mbbank.adminportal.core.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.com.mbbank.adminportal.common.model.response.PageImpl;
import vn.com.mbbank.adminportal.common.model.response.Response;
import vn.com.mbbank.adminportal.common.util.Pageables;
import vn.com.mbbank.adminportal.core.model.request.RoutingBankRequest;
import vn.com.mbbank.adminportal.core.model.response.RoutingBankResponse;
import vn.com.mbbank.adminportal.core.service.BankService;

import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/banks/**")
public class BankController {
  private final BankService bankService;

  @GetMapping
  public CompletableFuture<Response<PageImpl<RoutingBankResponse>>> get(@Valid RoutingBankRequest request, @RequestParam int page, @RequestParam int size, @RequestParam(required = false, defaultValue = "id:ASC") String sort) {
    return bankService.getBanks(request, Pageables.of(page, size, sort)).thenApply(Response::ofSucceeded);
  }
}
