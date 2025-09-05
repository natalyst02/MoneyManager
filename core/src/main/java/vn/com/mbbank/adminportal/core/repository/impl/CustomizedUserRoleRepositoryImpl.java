package vn.com.mbbank.adminportal.core.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import vn.com.mbbank.adminportal.common.exception.PaymentPlatformException;
import vn.com.mbbank.adminportal.common.util.CommonErrorCode;
import vn.com.mbbank.adminportal.core.model.entity.UserRole;
import vn.com.mbbank.adminportal.core.repository.CustomizedUserRoleRepository;
import vn.com.mbbank.adminportal.core.util.OracleHelper;


import java.sql.PreparedStatement;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CustomizedUserRoleRepositoryImpl implements CustomizedUserRoleRepository {
  private static final String DELETE_SQL_TEMPLATE = """
    DELETE FROM PAP_USER_ROLE WHERE ID IN (%s)
    RETURNING ID, USER_ID, ROLE_ID, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT
    INTO ?,?,?,?,?,?,?
    """;

  private final JdbcTemplate jdbcTemplate;

  @Override
  public Optional<List<UserRole>> deleteUserRole(List<Long> userRoleIds) {
    if (userRoleIds == null || userRoleIds.isEmpty()) {
      return Optional.of(Collections.emptyList());
    }

    String placeholders = userRoleIds.stream()
        .map(id -> "?")
        .collect(Collectors.joining(","));
    String deleteSql = String.format(DELETE_SQL_TEMPLATE, placeholders);

    return Optional.ofNullable(jdbcTemplate.execute(deleteSql, (PreparedStatement ps) -> {
      var ops = OracleHelper.unwrap(ps);
      int index = 1;

      for (Long id : userRoleIds) {
        ops.setLong(index++, id);
      }

      ops.registerReturnParameter(index++, Types.BIGINT);
      ops.registerReturnParameter(index++, Types.BIGINT);
      ops.registerReturnParameter(index++, Types.BIGINT);
      ops.registerReturnParameter(index++, Types.VARCHAR);
      ops.registerReturnParameter(index++, Types.TIMESTAMP_WITH_TIMEZONE);
      ops.registerReturnParameter(index++, Types.VARCHAR);
      ops.registerReturnParameter(index++, Types.TIMESTAMP_WITH_TIMEZONE);

      List<UserRole> deletedRoles = new ArrayList<>();
      if (ops.executeUpdate() > 0) {
        var rs = ops.getReturnResultSet();
        while (rs.next()) {
          var userRole = new UserRole()
              .setId(rs.getLong(1))
              .setUserId(rs.getLong(2))
              .setRoleId(rs.getLong(3))
              .setCreatedBy(rs.getString(4))
              .setCreatedAt(rs.getObject(5, OffsetDateTime.class))
              .setUpdatedBy(rs.getString(6))
              .setUpdatedAt(rs.getObject(7, OffsetDateTime.class));
          deletedRoles.add(userRole);
        }
      }
      return deletedRoles.isEmpty() ? null : deletedRoles;
    }));
  }
}
