package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.common.mapper.BeanMapper;
import vn.com.mbbank.adminportal.core.model.entity.WhitelistAccount;
import vn.com.mbbank.adminportal.core.model.response.WhitelistAccountResponse;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WhitelistAccount2WhitelistAccountResponseMapper extends BeanMapper<WhitelistAccount, WhitelistAccountResponse> {
  WhitelistAccount2WhitelistAccountResponseMapper INSTANCE = Mappers.getMapper(WhitelistAccount2WhitelistAccountResponseMapper.class);

  WhitelistAccountResponse map(WhitelistAccount entity);
}
