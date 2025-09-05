package vn.com.mbbank.adminportal.core.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.com.mbbank.adminportal.core.model.filter.TransferChannelLimitHistoryFilter;
import vn.com.mbbank.adminportal.core.model.response.TransferChannelLimitHistoryResp;

public interface TransferChannelLimitHistoryService {
    Page<TransferChannelLimitHistoryResp> getHistorys(TransferChannelLimitHistoryFilter filter, Pageable pageable);

    TransferChannelLimitHistoryResp getDetailHistory(Long historyId);
}

