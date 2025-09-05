package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.core.model.RoleInfo;
import vn.com.mbbank.adminportal.core.model.entity.User;
import vn.com.mbbank.adminportal.core.model.response.UserResponse;

import java.util.List;

@Mapper
public interface User2UserResponseMapper {
  User2UserResponseMapper INSTANCE = Mappers.getMapper(User2UserResponseMapper.class);
  @Mapping(target = "roles", source = "userRoleCode")
  UserResponse map(User source, List<RoleInfo> userRoleCode);
}