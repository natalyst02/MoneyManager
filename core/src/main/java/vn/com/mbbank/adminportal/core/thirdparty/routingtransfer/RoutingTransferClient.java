package vn.com.mbbank.adminportal.core.thirdparty.routingtransfer;

import org.springframework.data.domain.Pageable;
import vn.com.mbbank.adminportal.common.model.response.PageImpl;
import vn.com.mbbank.adminportal.core.model.request.RoutingBankRequest;
import vn.com.mbbank.adminportal.core.thirdparty.routingtransfer.model.response.GetBanksResponse;

import java.util.concurrent.CompletableFuture;

public interface RoutingTransferClient {
  CompletableFuture<PageImpl<GetBanksResponse>> getBanksAsync(RoutingBankRequest request, Pageable pageable);
}
