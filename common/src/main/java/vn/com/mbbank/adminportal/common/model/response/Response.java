package vn.com.mbbank.adminportal.common.model.response;

import com.dslplatform.json.CompiledJson;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import vn.com.mbbank.adminportal.common.exception.BusinessErrorCode;
import vn.com.mbbank.adminportal.common.exception.PaymentPlatformException;
import vn.com.mbbank.adminportal.common.model.FieldViolation;
import vn.com.mbbank.adminportal.common.util.Constant;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@CompiledJson
@Accessors(chain = true)
public class Response<T> {
  public static final String OK_CODE = Constant.PREFIX_RESPONSE_CODE + 200;
  private final int status;
  private final String error;
  private final String timestamp;
  private final String soaErrorCode;
  private String soaErrorDesc;
  private final String originalService;
  private final List<FieldViolation> errors;
  private final String clientMessageId;
  private final String path;
  private final T data;

  Response(HttpStatus httpStatus, String soaErrorCode, String soaErrorDesc, String originalService, List<FieldViolation> errors, T data) {
    this.status = httpStatus.value();
    this.error = httpStatus.getReasonPhrase();
    this.timestamp = Instant.now().toString();
    this.soaErrorCode = soaErrorCode;
    this.soaErrorDesc = soaErrorDesc;
    this.originalService = originalService;
    this.errors = errors;
    this.clientMessageId = ThreadContext.get(Constant.CLIENT_MESSAGE_ID);
    this.path = ThreadContext.get(Constant.PATH);
    this.data = data;
  }

  Response(HttpStatus httpStatus, String soaErrorCode, String soaErrorDesc, String originalService, List<FieldViolation> errors, String path, T data) {
    this.status = httpStatus.value();
    this.error = httpStatus.getReasonPhrase();
    this.timestamp = Instant.now().toString();
    this.soaErrorCode = soaErrorCode;
    this.soaErrorDesc = soaErrorDesc;
    this.originalService = originalService;
    this.errors = errors;
    this.clientMessageId = ThreadContext.get(Constant.CLIENT_MESSAGE_ID);
    this.path = path;
    this.data = data;
  }

  public Response(int status, String error, String timestamp, String soaErrorCode, String soaErrorDesc, String originalService, List<FieldViolation> errors, String clientMessageId, String path, T data) {
    this.status = status;
    this.error = error;
    this.timestamp = timestamp;
    this.soaErrorCode = soaErrorCode;
    this.soaErrorDesc = soaErrorDesc;
    this.originalService = originalService;
    this.errors = errors;
    this.clientMessageId = clientMessageId;
    this.path = path;
    this.data = data;
  }

  public static <T> Response<T> ofSucceeded() {
    return ofSucceeded((T) null);
  }

  public static <T> Response<T> ofSucceeded(T data) {
    return new Response<>(HttpStatus.OK, null, null, null, null, data);
  }

  public static Response<Void> ofFailed(BusinessErrorCode errorCode) {
    return ofFailed(errorCode, (String) null);
  }

  public static Response<Void> ofFailed(BusinessErrorCode errorCode, List<FieldViolation> errors) {
    return ofFailed(errorCode, null, errors);
  }

  public static Response<Void> ofFailed(BusinessErrorCode errorCode, String message) {
    return ofFailed(errorCode, message, null);
  }

  public static <T> Response<T> ofFailed(BusinessErrorCode errorCode, String message, T data) {
    return ofFailed(errorCode, message, null, data);
  }

  public static Response<Void> ofFailed(BusinessErrorCode errorCode, String message, List<FieldViolation> errors) {
    return new Response<>(errorCode.httpStatus(), Constant.PREFIX_RESPONSE_CODE + errorCode.code(), message, Constant.ORIGINAL_SERVICE, errors, null);
  }

  public static <T> Response<T> ofFailed(BusinessErrorCode errorCode, String message, List<FieldViolation> errors, T data) {
    return new Response<>(errorCode.httpStatus(), Constant.PREFIX_RESPONSE_CODE + errorCode.code(), message, Constant.ORIGINAL_SERVICE, errors, data);
  }

  public static Response<Void> ofFailed(PaymentPlatformException exception) {
    return ofFailed(exception.getErrorCode(), exception.getMessage());
  }

  public static <T> Response<PageImpl<T>> ofSucceeded(Page<T> data) {
    PageImpl<T> pageData = new PageImpl<>();
    pageData.content = data.getContent();
    pageData.page = data.getNumber();
    pageData.total = data.getTotalElements();
    pageData.size = data.getSize();
    return new Response<>(HttpStatus.OK, null, null, null, null, pageData);
  }
}