package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.common.mapper.BeanMapper;
import vn.com.mbbank.adminportal.core.model.entity.BlacklistAccount;
import vn.com.mbbank.adminportal.core.model.request.UpdateBlacklistAccountRequest;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UpdateBlacklistAccountReq2BlacklistAccount extends BeanMapper<UpdateBlacklistAccountRequest, BlacklistAccount> {
  UpdateBlacklistAccountReq2BlacklistAccount INSTANCE = Mappers.getMapper(UpdateBlacklistAccountReq2BlacklistAccount.class);
}
