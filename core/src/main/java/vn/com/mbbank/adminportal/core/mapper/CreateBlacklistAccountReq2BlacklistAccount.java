package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.common.mapper.BeanMapper;
import vn.com.mbbank.adminportal.core.model.entity.BlacklistAccount;
import vn.com.mbbank.adminportal.core.model.request.CreateBlacklistAccountRequest;

import java.time.OffsetDateTime;

@Mapper
public interface CreateBlacklistAccountReq2BlacklistAccount extends BeanMapper<CreateBlacklistAccountRequest, BlacklistAccount> {
  CreateBlacklistAccountReq2BlacklistAccount INSTANCE = Mappers.getMapper(CreateBlacklistAccountReq2BlacklistAccount.class);

  BlacklistAccount map(CreateBlacklistAccountRequest createBlacklistAccountRequest, String username);

  @AfterMapping
  default void afterMapping(String username, @MappingTarget BlacklistAccount target) {
    var now = OffsetDateTime.now();
    target.setCreatedBy(username)
        .setCreatedAt(now)
        .setUpdatedBy(username)
        .setUpdatedAt(now);
  }
}
