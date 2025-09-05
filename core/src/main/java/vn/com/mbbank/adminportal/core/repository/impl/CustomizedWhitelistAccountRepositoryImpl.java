package vn.com.mbbank.adminportal.core.repository.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import vn.com.mbbank.adminportal.core.model.ApprovalStatus;
import vn.com.mbbank.adminportal.core.model.TransferChannel;
import vn.com.mbbank.adminportal.core.model.entity.WhitelistAccount;
import vn.com.mbbank.adminportal.core.repository.CustomizedWhitelistAccountRepository;
import vn.com.mbbank.adminportal.core.util.OracleHelper;

import java.sql.PreparedStatement;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Log4j2
public class CustomizedWhitelistAccountRepositoryImpl implements CustomizedWhitelistAccountRepository {
  private static final String UPDATE_SQL = """
    UPDATE PAP_WHITELIST_ACCOUNT
    SET ACCOUNT_NO = ?,
        BANK_CODE = ?,
        TRANSFER_CHANNEL = ?,
        REASON = ?,
        APPROVAL_STATUS = ?,
        ACTIVE = ?,
        UPDATED_AT = ?,
        UPDATED_BY = ?,
        APPROVED_AT = ?,
        APPROVED_BY = ?
    WHERE ID = ?
    RETURNING ID, BANK_CODE, TRANSFER_CHANNEL, ACCOUNT_NO, REASON,APPROVAL_STATUS , ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT,APPROVED_BY,APPROVED_AT
    INTO ?,?,?,?,?,?,?,?,?,?,?,?,?
    """;
  private final JdbcTemplate jdbcTemplate;

  @Override
  public Optional<WhitelistAccount> updateAccount(WhitelistAccount whitelistAccount) {
    return jdbcTemplate.execute(UPDATE_SQL, (PreparedStatement ps) -> {
      var ops = OracleHelper.unwrap(ps);
      var pIdx = 1;
      ops.setString(pIdx, whitelistAccount.getAccountNo());
      ops.setString(++pIdx, whitelistAccount.getBankCode());
      ops.setString(
          ++pIdx,
          whitelistAccount.getTransferChannel() != null
              ? whitelistAccount.getTransferChannel().name()
              : null);
      ops.setString(++pIdx, whitelistAccount.getReason());
      ops.setString(
          ++pIdx,
          whitelistAccount.getApprovalStatus() != null
              ? whitelistAccount.getApprovalStatus().name()
              : null);
      ops.setInt(++pIdx, whitelistAccount.isActive() ? 1 : 0);
      ops.setObject(++pIdx, whitelistAccount.getUpdatedAt());
      ops.setString(++pIdx, whitelistAccount.getUpdatedBy());
      ops.setObject(++pIdx, whitelistAccount.getApprovedAt());
      ops.setString(++pIdx, whitelistAccount.getApprovedBy());
      ops.setLong(++pIdx, whitelistAccount.getId());

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
      ops.registerReturnParameter(++pIdx, Types.VARCHAR);
      ops.registerReturnParameter(++pIdx, Types.TIME_WITH_TIMEZONE);

      if (ops.executeUpdate() == 0) {
        return Optional.empty();
      }
      var rs = ops.getReturnResultSet();
      if (rs.next()) {
        var result =
            new WhitelistAccount()
                .setId(rs.getLong(1))
                .setBankCode(rs.getString(2))
                .setTransferChannel(
                    rs.getString(3) != null ? TransferChannel.valueOf(rs.getString(3)) : null)
                .setAccountNo(rs.getString(4))
                .setReason(rs.getString(5))
                .setApprovalStatus(
                    rs.getString(6) != null ? ApprovalStatus.valueOf(rs.getString(6)) : null)
                .setActive(rs.getBoolean(7))
                .setCreatedBy(rs.getString(8))
                .setCreatedAt(rs.getObject(9, OffsetDateTime.class))
                .setUpdatedBy(rs.getString(10))
                .setUpdatedAt(rs.getObject(11, OffsetDateTime.class))
                .setApprovedBy(rs.getString(12))
                .setApprovedAt(rs.getObject(13, OffsetDateTime.class));
        return Optional.of(result);
      }
      return Optional.empty();
    });
  }
}
