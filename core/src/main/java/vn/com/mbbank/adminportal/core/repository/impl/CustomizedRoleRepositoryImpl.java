package vn.com.mbbank.adminportal.core.repository.impl;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.runtime.Generics;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import vn.com.mbbank.adminportal.common.util.Json;
import vn.com.mbbank.adminportal.core.model.RoleType;
import vn.com.mbbank.adminportal.core.model.entity.Role;
import vn.com.mbbank.adminportal.core.repository.CustomizedRoleRepository;
import vn.com.mbbank.adminportal.core.util.OracleHelper;

import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CustomizedRoleRepositoryImpl implements CustomizedRoleRepository {
  private final JdbcTemplate jdbcTemplate;
  private static final JsonReader.ReadObject<Map<String, Integer>> ROLE_PERMISSION_READ_OBJECT = Json.findReader(Generics.makeParameterizedType(Map.class, String.class, Integer.class));

  private static final String UPDATE_SQL = """
      UPDATE PAP_ROLE
      SET NAME = ?,
          DESCRIPTION = ?,
          PERMISSIONS = ?,
          REASON = ?,
          ACTIVE = ?,
          UPDATED_BY = ?,
          UPDATED_AT = ?
      WHERE ID = ?
      RETURNING ID, CODE, NAME, TYPE, DESCRIPTION, PERMISSIONS, REASON, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT
      INTO ?,?,?,?,?,?,?,?,?,?,?,?
      """;
  @Override
  public Optional<Role> updateRole(Role role) {
    return jdbcTemplate.execute(UPDATE_SQL, (PreparedStatement ps) -> {
      var ops = OracleHelper.unwrap(ps);
      var pIdx = 1;
      ops.setString(pIdx, role.getName());
      ops.setString(++pIdx, role.getDescription());
      ops.setString(++pIdx, Json.encodeToString(role.getPermissions()));
      ops.setString(++pIdx, role.getReason());
      ops.setInt(++pIdx, role.isActive() ? 1 : 0);
      ops.setString(++pIdx, role.getUpdatedBy());
      ops.setObject(++pIdx, role.getUpdatedAt());

      ops.setLong(++pIdx, role.getId());

      ops.registerReturnParameter(++pIdx, Types.BIGINT);
      ops.registerReturnParameter(++pIdx, Types.VARCHAR);
      ops.registerReturnParameter(++pIdx, Types.VARCHAR);
      ops.registerReturnParameter(++pIdx, Types.VARCHAR);
      ops.registerReturnParameter(++pIdx, Types.VARCHAR);
      ops.registerReturnParameter(++pIdx, Types.VARCHAR);
      ops.registerReturnParameter(++pIdx, Types.VARCHAR);
      ops.registerReturnParameter(++pIdx, Types.INTEGER);
      ops.registerReturnParameter(++pIdx, Types.VARCHAR);
      ops.registerReturnParameter(++pIdx, Types.TIME_WITH_TIMEZONE);
      ops.registerReturnParameter(++pIdx, Types.VARCHAR);
      ops.registerReturnParameter(++pIdx, Types.TIME_WITH_TIMEZONE);
      if (ops.executeUpdate() == 0) {
        return Optional.empty();
      }
      var rs = ops.getReturnResultSet();
      if (rs.next()) {
        var result = new Role()
            .setId(rs.getLong(1))
            .setCode(rs.getString(2))
            .setName(rs.getString(3))
            .setType(rs.getString(4) != null ? RoleType.valueOf(rs.getString(4)) : null)
            .setDescription(rs.getString(5))
            .setPermissions(rs.getString(6) != null ? Json.decode(rs.getString(6).getBytes(StandardCharsets.UTF_8), ROLE_PERMISSION_READ_OBJECT): null)
            .setReason(rs.getString(7))
            .setActive(rs.getInt(8) == 1)
            .setCreatedBy(rs.getString(9))
            .setCreatedAt(rs.getObject(10, OffsetDateTime.class))
            .setUpdatedBy(rs.getString(11))
            .setUpdatedAt(rs.getObject(12, OffsetDateTime.class));
        return Optional.of(result);
      }
      return Optional.empty();
    });
  }
}
