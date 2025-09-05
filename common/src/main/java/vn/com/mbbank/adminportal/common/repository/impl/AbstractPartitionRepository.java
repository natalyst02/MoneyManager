package vn.com.mbbank.adminportal.common.repository.impl;

import jakarta.persistence.Table;
import org.springframework.jdbc.core.JdbcTemplate;
import vn.com.mbbank.adminportal.common.exception.PaymentPlatformException;
import vn.com.mbbank.adminportal.common.util.CommonErrorCode;

import java.lang.reflect.ParameterizedType;

public abstract class AbstractPartitionRepository<T> {
  private final String prefixSql;
  private final String suffixSql;
  private final JdbcTemplate jdbcTemplate;

  @SuppressWarnings("unchecked")
  public AbstractPartitionRepository(JdbcTemplate jdbcTemplate) {
    var superClass = this.getClass().getGenericSuperclass();
    var entityType = ((ParameterizedType) superClass).getActualTypeArguments()[0];
    try {
      Class<T> entityClass = entityType instanceof Class clazz ? clazz : (Class<T>) Class.forName(entityType.getTypeName());
      String tableName = entityClass.getAnnotation(Table.class).name();
      this.prefixSql = "BEGIN FOR cc IN (SELECT partition_name, high_value FROM user_tab_partitions WHERE table_name = '" + tableName +
                         "' AND partition_name != 'P0') LOOP EXECUTE IMMEDIATE 'BEGIN IF sysdate >= ADD_MONTHS(' || cc.high_value || ', ";
      this.suffixSql = ") THEN EXECUTE IMMEDIATE ''ALTER TABLE " + tableName + """
                                       DROP PARTITION ' || cc.partition_name || ' UPDATE INDEXES
                                       '';
                                       END IF;
                                    END;';
                                END LOOP;
                          END;
                          """;
      this.jdbcTemplate = jdbcTemplate;
    } catch (ClassNotFoundException e) {
      throw new PaymentPlatformException(CommonErrorCode.INTERNAL_SERVER_ERROR, "can't get entity class", e);
    }
  }

  public void dropPartitions(int olderMonths) {
    jdbcTemplate.execute(this.prefixSql + olderMonths + this.suffixSql);
  }
}