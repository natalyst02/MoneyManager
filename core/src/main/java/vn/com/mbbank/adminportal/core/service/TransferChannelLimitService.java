package vn.com.mbbank.adminportal.core.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import vn.com.mbbank.adminportal.core.model.filter.TransferChannelLimitFilter;
import vn.com.mbbank.adminportal.core.model.filter.TransferChannelLimitHistoryFilter;
import vn.com.mbbank.adminportal.core.model.request.CreateTransferChannelLimitRequest;
import vn.com.mbbank.adminportal.core.model.request.UpdateTransferChannelLimitRequest;
import vn.com.mbbank.adminportal.core.model.response.*;
import java.util.concurrent.CompletableFuture;

import java.util.List;

public interface TransferChannelLimitService {
    CompletableFuture<TransferChannelLimitResp> create(Authentication authentication, CreateTransferChannelLimitRequest request);

    CompletableFuture<TransferChannelLimitResp> update(Authentication authentication, Long id, UpdateTransferChannelLimitRequest request);

    List<TransferChannelLimitResp> getList(TransferChannelLimitFilter filter, String sort);

    TransferChannelLimitResp getDetail(Long id);

    Page<TransferChannelLimitHistoryResp> getListHistory(Long id, TransferChannelLimitHistoryFilter filter, Pageable pageable);
    TransferChannelLimitHistoryResp getDetailHistory(Long id);
}
