package vn.com.mbbank.adminportal.common.exception;

import lombok.Getter;

import java.util.Objects;

@Getter
public class PartialDataException extends NoStackTraceException {
  private final transient Object partialData;

  public PartialDataException(Object partialData, Throwable cause) {
    super(null, Objects.requireNonNull(cause));
    this.partialData = partialData;
  }
}
