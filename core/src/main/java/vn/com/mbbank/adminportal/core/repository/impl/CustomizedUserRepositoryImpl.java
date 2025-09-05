package vn.com.mbbank.adminportal.core.repository.impl;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.runtime.Generics;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import vn.com.mbbank.adminportal.common.util.Json;
import vn.com.mbbank.adminportal.core.model.RoleType;
import vn.com.mbbank.adminportal.core.model.entity.Role;
import vn.com.mbbank.adminportal.core.model.entity.User;
import vn.com.mbbank.adminportal.core.repository.CustomizedRoleRepository;
import vn.com.mbbank.adminportal.core.repository.CustomizedUserRepository;
import vn.com.mbbank.adminportal.core.util.OracleHelper;

import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CustomizedUserRepositoryImpl implements CustomizedUserRepository {
    private final JdbcTemplate jdbcTemplate;

    private static final String UPDATE_SQL = """
      UPDATE PAP_USER
      SET REASON = ?,
          ACTIVE = ?,
          UPDATED_BY = ?,
          UPDATED_AT = ?
      WHERE ID = ?
      RETURNING ID,
                USERNAME,
                KEYCLOAK_ID,
                EMPLOYEE_CODE,
                FULL_NAME,
                PHONE_NUMBER,
                EMAIL,
                JOB_NAME,
                ORG_NAME,
                REASON,
                ACTIVE,
                CREATED_BY,
                CREATED_AT,
                UPDATED_BY,
                UPDATED_AT
      INTO ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?
      """;
    @Override
    public Optional<User> updateUser(User user) {
        return jdbcTemplate.execute(UPDATE_SQL, (PreparedStatement ps) -> {
            var ops = OracleHelper.unwrap(ps);
            var pIdx = 1;
            ops.setString(pIdx, user.getReason());
            ops.setInt(++pIdx, user.isActive() ?  1 : 0);
            ops.setString(++pIdx, user.getUpdatedBy());
            ops.setObject(++pIdx, user.getUpdatedAt());

            ops.setLong(++pIdx, user.getId());

            ops.registerReturnParameter(++pIdx, Types.BIGINT);
            ops.registerReturnParameter(++pIdx, Types.VARCHAR);
            ops.registerReturnParameter(++pIdx, Types.VARCHAR);
            ops.registerReturnParameter(++pIdx, Types.VARCHAR);
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
                var result = new User()
                        .setId(rs.getLong(1))
                        .setUsername(rs.getString(2))
                        .setKeycloakId(rs.getString(3))
                        .setEmployeeCode(rs.getString(4) )
                        .setFullName(rs.getString(5))
                        .setPhoneNumber(rs.getString(6) )
                        .setEmail(rs.getString(7))
                        .setJobName(rs.getString(8))
                        .setOrgName(rs.getString(9))
                        .setReason(rs.getString(10))
                        .setActive(rs.getInt(11) == 1)
                        .setCreatedBy(rs.getString(12))
                        .setCreatedAt(rs.getObject(13, OffsetDateTime.class))
                        .setUpdatedBy(rs.getString(14))
                        .setUpdatedAt(rs.getObject(15, OffsetDateTime.class));
                return Optional.of(result);
            }
            return Optional.empty();
        });
    }
}