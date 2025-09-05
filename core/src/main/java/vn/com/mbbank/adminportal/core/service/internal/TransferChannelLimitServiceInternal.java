package vn.com.mbbank.adminportal.core.service.internal;

import vn.com.mbbank.adminportal.core.model.entity.AliasAccount;
import vn.com.mbbank.adminportal.core.model.entity.TransferChannelLimit;
import vn.com.mbbank.adminportal.core.service.TransferChannelLimitService;

public interface TransferChannelLimitServiceInternal extends TransferChannelLimitService {
    TransferChannelLimit create0(TransferChannelLimit account);

    TransferChannelLimit updateReturning(TransferChannelLimit account);
}
