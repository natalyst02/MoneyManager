package vn.com.mbbank.adminportal.core.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.com.mbbank.adminportal.common.model.response.PageImpl;
import vn.com.mbbank.adminportal.core.model.filter.BankFilter;
import vn.com.mbbank.adminportal.core.model.request.RoutingBankRequest;
import vn.com.mbbank.adminportal.core.model.response.BankResponse;
import vn.com.mbbank.adminportal.core.model.response.RoutingBankResponse;

import java.util.concurrent.CompletableFuture;

public interface BankService {
  Page<BankResponse> get(BankFilter bankFilter, Pageable pageable);

  CompletableFuture<PageImpl<RoutingBankResponse>> getBanks(RoutingBankRequest request,
                                                            Pageable pageable);
}
