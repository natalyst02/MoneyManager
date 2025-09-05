package vn.com.mbbank.adminportal.core.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.com.mbbank.adminportal.common.exception.PaymentPlatformException;
import vn.com.mbbank.adminportal.core.mapper.TransferChannelLimitHistory2TransferChannelLimitHistoryRespMapper;
import vn.com.mbbank.adminportal.core.model.filter.TransferChannelLimitHistoryFilter;
import vn.com.mbbank.adminportal.core.model.response.TransferChannelLimitHistoryResp;
import vn.com.mbbank.adminportal.core.repository.TransferChannelLimitHistoryRepository;
import vn.com.mbbank.adminportal.core.service.TransferChannelLimitHistoryService;
import vn.com.mbbank.adminportal.core.util.ErrorCode;
import vn.com.mbbank.adminportal.core.util.Filters;

@Service
@RequiredArgsConstructor
@Log4j2
public class TransferChannelLimitHistoryServiceImpl implements TransferChannelLimitHistoryService {
    private final TransferChannelLimitHistoryRepository transferChannelLimitHistoryRepository;

    @Override
    public Page<TransferChannelLimitHistoryResp> getHistorys(TransferChannelLimitHistoryFilter filter, Pageable pageable) {
        return transferChannelLimitHistoryRepository.findAll(Filters.toSpecification(filter), pageable).map(TransferChannelLimitHistory2TransferChannelLimitHistoryRespMapper.INSTANCE::map);
    }

    @Override
    public TransferChannelLimitHistoryResp getDetailHistory(Long historyId) {
        return TransferChannelLimitHistory2TransferChannelLimitHistoryRespMapper.INSTANCE.map(transferChannelLimitHistoryRepository.findById(historyId)
                .orElseThrow(() -> new PaymentPlatformException(ErrorCode.TRANSFER_CHANNEL_LIMIT_HISTORY_NOT_FOUND, ErrorCode.TRANSFER_CHANNEL_LIMIT_HISTORY_NOT_FOUND.message())));
    }
}
