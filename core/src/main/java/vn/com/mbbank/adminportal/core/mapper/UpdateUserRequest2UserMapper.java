package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.common.mapper.BeanMapper;
import vn.com.mbbank.adminportal.core.model.entity.User;
import vn.com.mbbank.adminportal.core.model.request.UpdateUserRequest;

@Mapper
public interface UpdateUserRequest2UserMapper extends BeanMapper<UpdateUserRequest, User> {
    UpdateUserRequest2UserMapper INSTANCE = Mappers.getMapper(UpdateUserRequest2UserMapper.class);
}