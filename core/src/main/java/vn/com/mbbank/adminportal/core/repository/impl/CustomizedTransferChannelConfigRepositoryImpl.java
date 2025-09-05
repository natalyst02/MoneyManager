package vn.com.mbbank.adminportal.core.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import vn.com.mbbank.adminportal.core.model.TransferChannel;
import vn.com.mbbank.adminportal.core.model.entity.TransferChannelConfig;
import vn.com.mbbank.adminportal.core.model.request.UpdateTransferChannelConfigPrioritiesRequest;
import vn.com.mbbank.adminportal.core.model.request.UpdateTransferChannelConfigStatusRequest;
import vn.com.mbbank.adminportal.core.repository.CustomizedTransferChannelConfigRepository;
import vn.com.mbbank.adminportal.core.util.OracleHelper;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CustomizedTransferChannelConfigRepositoryImpl implements CustomizedTransferChannelConfigRepository {
  private static final String UPDATE_PRIORITY_SQL = """
      UPDATE PAP_TRANSFER_CHANNEL_CONFIG
      SET PRIORITY = ?,
          REASON = ?,
          UPDATED_AT = ?,
          UPDATED_BY = ?
      WHERE ID = ?
      """;
  private static final String UPDATE_STATUS_SQL = """
      UPDATE PAP_TRANSFER_CHANNEL_CONFIG
      SET ACTIVE = ?,
          REASON = ?,
          UPDATED_AT = ?,
          UPDATED_BY = ?
      WHERE ID = ?
      RETURNING ID, TRANSFER_CHANNEL, PRIORITY, REASON, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT
      INTO ?,?,?,?,?,?,?,?,?
      """;
  private final JdbcTemplate jdbcTemplate;

  @Override
  public int[] updatePriorities(UpdateTransferChannelConfigPrioritiesRequest request, String updatedBy, OffsetDateTime updatedAt) {
    var priorities = request.getPriorities();
    var reason = request.getReason();
    return jdbcTemplate.batchUpdate(UPDATE_PRIORITY_SQL, new BatchPreparedStatementSetter() {
      @Override
      public void setValues(PreparedStatement ps, int i) throws SQLException {
        var priorityReference = priorities.get(i);
        var pIdx = 1;
        ps.setInt(pIdx, priorityReference.getPriority());
        ps.setString(++pIdx, reason);
        ps.setObject(++pIdx, updatedAt, Types.TIMESTAMP_WITH_TIMEZONE);
        ps.setString(++pIdx, updatedBy);
        ps.setLong(++pIdx, priorityReference.getId());
      }
      @Override
      public int getBatchSize() {
        return priorities.size();
      }
    });
  }

  @Override
  public Optional<TransferChannelConfig> updateStatus(UpdateTransferChannelConfigStatusRequest request, String updatedBy, OffsetDateTime updatedAt) {
    return jdbcTemplate.execute(UPDATE_STATUS_SQL, (PreparedStatement ps) -> {
      var ops = OracleHelper.unwrap(ps);
      var pIdx = 1;
      ops.setInt(pIdx, request.getActive() ? 1 : 0);
      ops.setString(++pIdx, request.getReason());
      ops.setObject(++pIdx, updatedAt, Types.TIMESTAMP_WITH_TIMEZONE);
      ops.setString(++pIdx, updatedBy);
      ops.setLong(++pIdx, request.getId());

      ops.registerReturnParameter(++pIdx, Types.BIGINT);
      ops.registerReturnParameter(++pIdx, Types.VARCHAR);
      ops.registerReturnParameter(++pIdx, Types.INTEGER);
      ops.registerReturnParameter(++pIdx, Types.VARCHAR);
      ops.registerReturnParameter(++pIdx, Types.BIT);
      ops.registerReturnParameter(++pIdx, Types.VARCHAR);
      ops.registerReturnParameter(++pIdx, Types.TIME_WITH_TIMEZONE);
      ops.registerReturnParameter(++pIdx, Types.VARCHAR);
      ops.registerReturnParameter(++pIdx, Types.TIME_WITH_TIMEZONE);

      if (ops.executeUpdate() == 0) {
        return Optional.empty();
      }
      var rs = ops.getReturnResultSet();
      if (rs.next()) {
        var result = new TransferChannelConfig()
            .setId(rs.getLong(1))
            .setTransferChannel(rs.getString(2) != null ? TransferChannel.valueOf(rs.getString(2)) : null)
            .setPriority(rs.getInt(3))
            .setReason(rs.getString(4))
            .setActive(rs.getBoolean(5))
            .setCreatedBy(rs.getString(6))
            .setCreatedAt(rs.getObject(7, OffsetDateTime.class))
            .setUpdatedBy(rs.getString(8))
            .setUpdatedAt(rs.getObject(9, OffsetDateTime.class));
        return Optional.of(result);
      }
      return Optional.empty();
    });
  }
}
