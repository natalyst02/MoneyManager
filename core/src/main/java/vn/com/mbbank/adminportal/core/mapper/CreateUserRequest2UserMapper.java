package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.common.mapper.BeanMapper;
import vn.com.mbbank.adminportal.core.model.entity.User;
import vn.com.mbbank.adminportal.core.model.request.CreateUserRequest;

@Mapper
public interface CreateUserRequest2UserMapper extends BeanMapper<CreateUserRequest, User> {
    CreateUserRequest2UserMapper INSTANCE = Mappers.getMapper(CreateUserRequest2UserMapper.class);
    @Mapping(target = "active", defaultValue = "false")
    User map(CreateUserRequest source);
}



