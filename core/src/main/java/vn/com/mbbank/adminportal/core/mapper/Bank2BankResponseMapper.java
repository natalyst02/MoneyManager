package vn.com.mbbank.adminportal.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import vn.com.mbbank.adminportal.common.mapper.BeanMapper;
import vn.com.mbbank.adminportal.core.model.entity.Bank;
import vn.com.mbbank.adminportal.core.model.response.BankResponse;

@Mapper
public interface Bank2BankResponseMapper extends BeanMapper<Bank, BankResponse> {
  Bank2BankResponseMapper INSTANCE = Mappers.getMapper(Bank2BankResponseMapper.class);
}
