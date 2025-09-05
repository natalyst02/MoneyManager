package vn.com.mbbank.adminportal.common.model.filter;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;
import vn.com.mbbank.adminportal.common.model.ErrorMessageStatus;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Accessors(chain = true)
public class ErrorMessageFilter {
  private List<String> topics;
  private List<String> keys;
  private List<ErrorMessageStatus> statuses;
  private String messageText;
  private String errorText;
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime createdAtFrom;
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private OffsetDateTime createdAtTo;
}
