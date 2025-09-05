package vn.com.mbbank.adminportal.core.repository.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import vn.com.mbbank.adminportal.core.model.AccountEntryType;
import vn.com.mbbank.adminportal.core.model.TransactionType;
import vn.com.mbbank.adminportal.core.model.entity.BlacklistAccount;
import vn.com.mbbank.adminportal.core.repository.CustomizedBlacklistAccountRepository;
import vn.com.mbbank.adminportal.core.util.OracleHelper;

import java.sql.PreparedStatement;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Log4j2
public class CustomizedBlacklistAccountRepositoryImpl implements CustomizedBlacklistAccountRepository {
  private static final String UPDATE_SQL = """
      UPDATE PAP_BLACKLIST_ACCOUNT
      SET ACCOUNT_NO = ?,
          BANK_CODE = ?,
          TYPE = ?,
          TRANSACTION_TYPE = ?,
          REASON = ?,
          ACTIVE = ?,
          UPDATED_AT = ?,
          UPDATED_BY = ?
      WHERE ID = ?
      RETURNING ID, ACCOUNT_NO, BANK_CODE, TYPE, TRANSACTION_TYPE, REASON, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT
      INTO ?,?,?,?,?,?,?,?,?,?,?
      """;
  private final JdbcTemplate jdbcTemplate;

  @Override
  public Optional<BlacklistAccount> updateAccount(BlacklistAccount blacklistAccount) {
    return jdbcTemplate.execute(UPDATE_SQL, (PreparedStatement ps) -> {
      var ops = OracleHelper.unwrap(ps);
      var pIdx = 1;
      ps.setString(pIdx, blacklistAccount.getAccountNo());
      ps.setString(++pIdx, blacklistAccount.getBankCode());
      ops.setString(++pIdx, blacklistAccount.getType() != null ? blacklistAccount.getType().name() : null);
      ops.setString(++pIdx, blacklistAccount.getTransactionType() != null ? blacklistAccount.getTransactionType().name() : null);
      ops.setString(++pIdx, blacklistAccount.getReason());
      ops.setInt(++pIdx, blacklistAccount.isActive() ? 1 : 0);
      ops.setObject(++pIdx, blacklistAccount.getUpdatedAt());
      ops.setString(++pIdx, blacklistAccount.getUpdatedBy());
      ops.setLong(++pIdx, blacklistAccount.getId());

      ops.registerReturnParameter(++pIdx, Types.BIGINT);
      ops.registerReturnParameter(++pIdx, Types.VARCHAR);
      ops.registerReturnParameter(++pIdx, Types.VARCHAR);
      ops.registerReturnParameter(++pIdx, Types.VARCHAR);
      ops.registerReturnParameter(++pIdx, Types.VARCHAR);
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
        var result = new BlacklistAccount()
            .setId(rs.getLong(1))
            .setAccountNo(rs.getString(2))
            .setBankCode(rs.getString(3))
            .setType(rs.getString(4) != null ? AccountEntryType.valueOf(rs.getString(4)) : null)
            .setTransactionType(rs.getString(5) != null ? TransactionType.valueOf(rs.getString(5)) : null)
            .setReason(rs.getString(6))
            .setActive(rs.getBoolean(7))
            .setCreatedBy(rs.getString(8))
            .setCreatedAt(rs.getObject(9, OffsetDateTime.class))
            .setUpdatedBy(rs.getString(10))
            .setUpdatedAt(rs.getObject(11, OffsetDateTime.class));
        return Optional.of(result);
      }
      return Optional.empty();
    });
  }
}
