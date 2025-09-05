package vn.com.mbbank.adminportal.core.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import vn.com.mbbank.adminportal.core.model.ApprovalStatus;
import vn.com.mbbank.adminportal.core.model.PartnerType;
import vn.com.mbbank.adminportal.core.model.entity.AliasAccount;
import vn.com.mbbank.adminportal.core.repository.CustomizedAliasAccountRepository;
import vn.com.mbbank.adminportal.core.util.OracleHelper;

import java.sql.PreparedStatement;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CustomizedAliasAccountRepositoryImpl implements CustomizedAliasAccountRepository {
  private final JdbcTemplate jdbcTemplate;

  private static final String UPDATE_SQL = """
      UPDATE PAP_ALIAS_ACCOUNT
      SET PARTNER_TYPE = ?,
          PARTNER_ACCOUNT = ?,
          GET_NAME_URL = ?,
          CONFIRM_URL = ?,
          PROTOCOL = ?,
          CHANNEL = ?,
          REGEX = ?,
          MIN_TRANS_LIMIT = ?,
          MAX_TRANS_LIMIT = ?,
          PARTNER_PUBLIC_KEY = ?,
          MB_PRIVATE_KEY = ?,
          IS_RETRY_CONFIRM = ?,
          REASON = ?,
          ACTIVE = ?,
          UPDATED_BY = ?,
          UPDATED_AT = ?,
          APPROVAL_STATUS = ?,
          APPROVED_BY = ?,
          APPROVED_AT = ?
      WHERE ID = ?
      RETURNING ID, NAME, PARTNER_TYPE, PARTNER_ACCOUNT, GET_NAME_URL, CONFIRM_URL, PROTOCOL, CHANNEL, REGEX, MIN_TRANS_LIMIT, MAX_TRANS_LIMIT,
                PARTNER_PUBLIC_KEY, MB_PRIVATE_KEY, IS_RETRY_CONFIRM, REASON, ACTIVE, CREATED_BY, CREATED_AT, UPDATED_BY, UPDATED_AT, APPROVAL_STATUS, APPROVED_BY, APPROVED_AT
      INTO ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?
      """;

  @Override
  public Optional<AliasAccount> updateAliasAccount(AliasAccount account) {
    return jdbcTemplate.execute(UPDATE_SQL, (PreparedStatement ps) -> {
      var ops = OracleHelper.unwrap(ps);
      var pIdx = 1;
      ops.setString(pIdx, account.getPartnerType() != null ? account.getPartnerType().name() : null);
      ops.setString(++pIdx, account.getPartnerAccount());
      ops.setString(++pIdx, account.getGetNameUrl());
      ops.setString(++pIdx, account.getConfirmUrl());
      ops.setString(++pIdx, account.getProtocol());
      ops.setString(++pIdx, account.getChannel());
      ops.setString(++pIdx, account.getRegex());
      if (account.getMinTransLimit() != null) {
        ops.setLong(++pIdx, account.getMinTransLimit());
      } else {
        ops.setObject(++pIdx, null);
      }
      if (account.getMaxTransLimit() != null) {
        ops.setLong(++pIdx, account.getMaxTransLimit());
      } else {
        ops.setObject(++pIdx, null);
      }
      ops.setString(++pIdx, account.getPartnerPublicKey());
      ops.setString(++pIdx, account.getMbPrivateKey());
      ops.setInt(++pIdx, account.getIsRetryConfirm() ? 1 : 0);
      ops.setString(++pIdx, account.getReason());
      ops.setInt(++pIdx, account.getActive() ? 1 : 0);
      ops.setString(++pIdx, account.getUpdatedBy());
      ops.setObject(++pIdx, account.getUpdatedAt());
      ops.setString(++pIdx, account.getApprovalStatus() != null ? account.getApprovalStatus().name() : null);
      ops.setString(++pIdx, account.getApprovedBy());
      ops.setObject(++pIdx, account.getApprovedAt());

      ops.setLong(++pIdx, account.getId());

      ops.registerReturnParameter(++pIdx, Types.BIGINT);
      ops.registerReturnParameter(++pIdx, Types.VARCHAR);
      ops.registerReturnParameter(++pIdx, Types.VARCHAR);
      ops.registerReturnParameter(++pIdx, Types.VARCHAR);
      ops.registerReturnParameter(++pIdx, Types.VARCHAR);
      ops.registerReturnParameter(++pIdx, Types.VARCHAR);
      ops.registerReturnParameter(++pIdx, Types.VARCHAR);
      ops.registerReturnParameter(++pIdx, Types.VARCHAR);
      ops.registerReturnParameter(++pIdx, Types.VARCHAR);
      ops.registerReturnParameter(++pIdx, Types.LONGVARCHAR);
      ops.registerReturnParameter(++pIdx, Types.LONGVARCHAR);
      ops.registerReturnParameter(++pIdx, Types.VARCHAR);
      ops.registerReturnParameter(++pIdx, Types.VARCHAR);
      ops.registerReturnParameter(++pIdx, Types.INTEGER);
      ops.registerReturnParameter(++pIdx, Types.VARCHAR);
      ops.registerReturnParameter(++pIdx, Types.INTEGER);
      ops.registerReturnParameter(++pIdx, Types.VARCHAR);
      ops.registerReturnParameter(++pIdx, Types.TIME_WITH_TIMEZONE);
      ops.registerReturnParameter(++pIdx, Types.VARCHAR);
      ops.registerReturnParameter(++pIdx, Types.TIME_WITH_TIMEZONE);
      ops.registerReturnParameter(++pIdx, Types.VARCHAR);
      ops.registerReturnParameter(++pIdx, Types.VARCHAR);
      ops.registerReturnParameter(++pIdx, Types.TIME_WITH_TIMEZONE);
      if (ops.executeUpdate() == 0) {
        return Optional.empty();
      }
      var rs = ops.getReturnResultSet();
      if (rs.next()) {
        var result = new AliasAccount()
            .setId(rs.getLong(1))
            .setName(rs.getString(2))
            .setPartnerType(rs.getString(3) != null ? PartnerType.valueOf(rs.getString(3)) : null)
            .setPartnerAccount(rs.getString(4))
            .setGetNameUrl(rs.getString(5))
            .setConfirmUrl(rs.getString(6))
            .setProtocol(rs.getString(7))
            .setChannel(rs.getString(8))
            .setRegex(rs.getString(9))
            .setMinTransLimit(rs.getString(10) == null ? null : Long.valueOf(rs.getString(10)))
            .setMaxTransLimit(rs.getString(11) == null ? null : Long.valueOf(rs.getString(11)))
            .setPartnerPublicKey(rs.getString(12))
            .setMbPrivateKey(rs.getString(13))
            .setIsRetryConfirm(rs.getInt(14) == 1)
            .setReason(rs.getString(15))
            .setActive(rs.getInt(16) == 1)
            .setCreatedBy(rs.getString(17))
            .setCreatedAt(rs.getObject(18, OffsetDateTime.class))
            .setUpdatedBy(rs.getString(19))
            .setUpdatedAt(rs.getObject(20, OffsetDateTime.class))
            .setApprovalStatus(rs.getString(21) !=  null ? ApprovalStatus.valueOf(rs.getString(21)) : null)
            .setApprovedBy(rs.getString(22))
            .setApprovedAt(rs.getObject(23, OffsetDateTime.class));
        return Optional.of(result);
      }
      return Optional.empty();
    });
  }
}
