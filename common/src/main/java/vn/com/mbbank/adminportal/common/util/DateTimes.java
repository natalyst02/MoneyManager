package vn.com.mbbank.adminportal.common.util;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public final class DateTimes {
  public static final ZoneOffset BUSINESS_ZONE_OFFSET = ZoneOffset.of("+7");

  public static boolean isSameBusinessDay(OffsetDateTime odt1, OffsetDateTime odt2) {
    var businessDate1 = odt1.withOffsetSameInstant(BUSINESS_ZONE_OFFSET).toLocalDate();
    var businessDate2 = odt2.withOffsetSameInstant(BUSINESS_ZONE_OFFSET).toLocalDate();
    return businessDate1.equals(businessDate2);
  }

  private DateTimes() {
    throw new UnsupportedOperationException();
  }
}
