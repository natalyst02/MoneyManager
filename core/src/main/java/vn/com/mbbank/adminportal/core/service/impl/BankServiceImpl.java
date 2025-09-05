package vn.com.mbbank.adminportal.core.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.com.mbbank.adminportal.common.model.response.PageImpl;
import vn.com.mbbank.adminportal.core.mapper.Bank2BankResponseMapper;
import vn.com.mbbank.adminportal.core.mapper.GetBanksResponse2RoutingBankResponseMapper;
import vn.com.mbbank.adminportal.core.model.filter.BankFilter;
import vn.com.mbbank.adminportal.core.model.request.RoutingBankRequest;
import vn.com.mbbank.adminportal.core.model.response.BankResponse;
import vn.com.mbbank.adminportal.core.model.response.RoutingBankResponse;
import vn.com.mbbank.adminportal.core.repository.BankRepository;
import vn.com.mbbank.adminportal.core.service.internal.BankServiceInternal;
import vn.com.mbbank.adminportal.core.thirdparty.routingtransfer.RoutingTransferClient;
import vn.com.mbbank.adminportal.core.util.Filters;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class BankServiceImpl implements BankServiceInternal {
  private final BankRepository bankRepository;
  private final RoutingTransferClient routingTransferClient;

  @Override
  public Page<BankResponse> get(BankFilter bankFilter, Pageable pageable) {
    return bankRepository.findAll(Filters.toSpecification(bankFilter), pageable).map(Bank2BankResponseMapper.INSTANCE::map);
  }

  @Override
  public CompletableFuture<PageImpl<RoutingBankResponse>> getBanks(RoutingBankRequest request, Pageable pageable) {
    return routingTransferClient.getBanksAsync(request, pageable)
        .thenApply(response -> {
          var bankResponses = GetBanksResponse2RoutingBankResponseMapper.INSTANCE.map(response.getContent());
          return new PageImpl<>(response.getPage(), response.getSize(), response.getTotal(), bankResponses);
        });
  }
}
