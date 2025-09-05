package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.common.mapper.BeanMapper;
import vn.com.mbbank.adminportal.core.model.Action;
import vn.com.mbbank.adminportal.core.model.entity.UserRole;
import vn.com.mbbank.adminportal.core.model.entity.UserRoleHistory;


import java.time.OffsetDateTime;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {OffsetDateTime.class})
public interface UserRole2UserRoleHistory extends BeanMapper<UserRole, UserRoleHistory> {
  UserRole2UserRoleHistory INSTANCE = Mappers.getMapper(UserRole2UserRoleHistory.class);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "userRoleId", source = "userRole.id")
  @Mapping(target = "createdBy", source = "username")
  @Mapping(target = "createdAt", source = "timestamp")
  @Mapping(target = "updatedBy", source = "username")
  @Mapping(target = "updatedAt", source = "timestamp")
  UserRoleHistory map(Action action, String username, UserRole userRole,OffsetDateTime timestamp);
}
