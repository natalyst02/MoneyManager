package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.common.mapper.BeanMapper;
import vn.com.mbbank.adminportal.core.model.entity.BlacklistAccountHistory;
import vn.com.mbbank.adminportal.core.model.response.BlacklistAccountHistoryResponse;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BlacklistAccountHistory2BlacklistAccountHistoryResponseMapper extends BeanMapper<BlacklistAccountHistory, BlacklistAccountHistoryResponse> {
  BlacklistAccountHistory2BlacklistAccountHistoryResponseMapper INSTANCE = Mappers.getMapper(BlacklistAccountHistory2BlacklistAccountHistoryResponseMapper.class);
}
