package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.common.mapper.BeanMapper;
import vn.com.mbbank.adminportal.core.model.entity.WhitelistAccountHistory;
import vn.com.mbbank.adminportal.core.model.response.WhitelistAccountHistoryResponse;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WhitelistAccountHistory2WhitelistAccountHistoryResponseMapper extends BeanMapper<WhitelistAccountHistory, WhitelistAccountHistoryResponse> {
  WhitelistAccountHistory2WhitelistAccountHistoryResponseMapper INSTANCE = Mappers.getMapper(WhitelistAccountHistory2WhitelistAccountHistoryResponseMapper.class);

  WhitelistAccountHistoryResponse map(WhitelistAccountHistory entity);
}
