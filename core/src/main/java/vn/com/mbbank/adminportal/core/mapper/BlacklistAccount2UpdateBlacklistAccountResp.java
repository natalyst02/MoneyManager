package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.common.mapper.BeanMapper;
import vn.com.mbbank.adminportal.core.model.entity.BlacklistAccount;
import vn.com.mbbank.adminportal.core.model.response.BlacklistAccountResponse;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BlacklistAccount2UpdateBlacklistAccountResp extends BeanMapper<BlacklistAccount, BlacklistAccountResponse> {
  BlacklistAccount2UpdateBlacklistAccountResp INSTANCE = Mappers.getMapper(BlacklistAccount2UpdateBlacklistAccountResp.class);

  BlacklistAccountResponse map(BlacklistAccount blacklistAccount);
}
