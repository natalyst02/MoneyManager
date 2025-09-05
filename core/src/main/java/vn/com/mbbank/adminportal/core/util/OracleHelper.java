package vn.com.mbbank.adminportal.core.util;

import com.zaxxer.hikari.pool.HikariProxyPreparedStatement;
import oracle.jdbc.OraclePreparedStatement;
import vn.com.mbbank.adminportal.common.exception.PaymentPlatformException;
import vn.com.mbbank.adminportal.common.util.CommonErrorCode;


import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OracleHelper {
  public static OraclePreparedStatement unwrap(PreparedStatement preparedStatement) {
    try {
      if (preparedStatement instanceof HikariProxyPreparedStatement proxy) {
        return proxy.unwrap(OraclePreparedStatement.class);
      }
      return (OraclePreparedStatement) preparedStatement;
    } catch (SQLException e) {
      throw new PaymentPlatformException(CommonErrorCode.INTERNAL_SERVER_ERROR, "can't unwrap PreparedStatement to get OraclePreparedStatement", e);
    }
  }
}