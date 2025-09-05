package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.common.mapper.BeanMapper;
import vn.com.mbbank.adminportal.core.model.entity.AliasAccountHistory;
import vn.com.mbbank.adminportal.core.model.response.AliasAccountHistoryResp;

@Mapper
public interface AliasAccountHistory2AliasAccountHistoryRespMapper extends BeanMapper<AliasAccountHistory, AliasAccountHistoryResp> {
  AliasAccountHistory2AliasAccountHistoryRespMapper INSTANCE = Mappers.getMapper(AliasAccountHistory2AliasAccountHistoryRespMapper.class);
}
