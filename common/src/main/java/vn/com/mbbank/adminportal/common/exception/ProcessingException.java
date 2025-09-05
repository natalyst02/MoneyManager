package vn.com.mbbank.adminportal.common.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class ProcessingException extends NoStackTraceException {
  private final transient List<Throwable> throwables;

  public ProcessingException(List<Throwable> throwables) {
    super("", null);
    this.throwables = throwables;
  }
}
