package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.common.mapper.BeanMapper;
import vn.com.mbbank.adminportal.core.model.entity.User;
import vn.com.mbbank.adminportal.core.model.response.UpdateUserResponse;

@Mapper
public interface User2UpdateUserResponseMapper extends BeanMapper<User, UpdateUserResponse> {
    User2UpdateUserResponseMapper INSTANCE = Mappers.getMapper(User2UpdateUserResponseMapper.class);
}