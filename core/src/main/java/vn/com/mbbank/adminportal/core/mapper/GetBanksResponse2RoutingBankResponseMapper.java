package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.common.mapper.BeanMapper;
import vn.com.mbbank.adminportal.core.model.response.RoutingBankResponse;
import vn.com.mbbank.adminportal.core.thirdparty.routingtransfer.model.response.GetBanksResponse;

@Mapper
public interface GetBanksResponse2RoutingBankResponseMapper extends BeanMapper<GetBanksResponse, RoutingBankResponse> {
  GetBanksResponse2RoutingBankResponseMapper INSTANCE = Mappers.getMapper(GetBanksResponse2RoutingBankResponseMapper.class);
}
