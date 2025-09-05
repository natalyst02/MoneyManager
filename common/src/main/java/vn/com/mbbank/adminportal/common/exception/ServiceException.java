package vn.com.mbbank.adminportal.common.exception;

import vn.com.mbbank.adminportal.common.model.response.Response;

public class ServiceException extends NoStackTraceException {
  private final Response<?> errorResponse;

  public ServiceException(Response<?> errorResponse) {
    super(errorResponse.getSoaErrorCode() + " - " + errorResponse.getSoaErrorDesc());
    this.errorResponse = errorResponse;
  }

  @SuppressWarnings("unchecked")
  public <T> Response<T> getErrorResponse() {
    return (Response<T>) errorResponse;
  }
}
