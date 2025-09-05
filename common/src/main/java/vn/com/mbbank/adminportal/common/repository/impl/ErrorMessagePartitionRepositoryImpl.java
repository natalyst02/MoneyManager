package vn.com.mbbank.adminportal.common.repository.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import vn.com.mbbank.adminportal.common.model.entity.ErrorMessage;

@Component
public class ErrorMessagePartitionRepositoryImpl extends AbstractPartitionRepository<ErrorMessage> {
  public ErrorMessagePartitionRepositoryImpl(JdbcTemplate jdbcTemplate) {
    super(jdbcTemplate);
  }
}
