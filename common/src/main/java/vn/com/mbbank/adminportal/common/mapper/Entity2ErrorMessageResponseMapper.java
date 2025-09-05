package vn.com.mbbank.adminportal.common.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.common.model.entity.ErrorMessage;
import vn.com.mbbank.adminportal.common.model.response.ErrorMessageResponse;

@Mapper
public interface Entity2ErrorMessageResponseMapper extends BeanMapper<ErrorMessage, ErrorMessageResponse> {
  Entity2ErrorMessageResponseMapper INSTANCE = Mappers.getMapper(Entity2ErrorMessageResponseMapper.class);
}
