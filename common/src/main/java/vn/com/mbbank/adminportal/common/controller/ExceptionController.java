package vn.com.mbbank.adminportal.common.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ElementKind;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import vn.com.mbbank.adminportal.common.exception.BusinessErrorCode;
import vn.com.mbbank.adminportal.common.exception.PartialDataException;
import vn.com.mbbank.adminportal.common.exception.PaymentPlatformException;
import vn.com.mbbank.adminportal.common.exception.ServiceException;
import vn.com.mbbank.adminportal.common.exception.ValidateException;
import vn.com.mbbank.adminportal.common.model.FieldViolation;
import vn.com.mbbank.adminportal.common.model.HttpMessage;
import vn.com.mbbank.adminportal.common.model.response.Response;
import vn.com.mbbank.adminportal.common.service.internal.ErrorMappingConfigServiceInternal;
import vn.com.mbbank.adminportal.common.util.CommonErrorCode;
import vn.com.mbbank.adminportal.common.util.CompletableFutures;
import vn.com.mbbank.adminportal.common.util.LoggingHelper;
import vn.com.mbbank.adminportal.common.util.SerializationUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionException;

@Log4j2
@ControllerAdvice
@RequiredArgsConstructor
public class ExceptionController {
  private final ObjectMapper objectMapper;
  private final ErrorMappingConfigServiceInternal errorMappingConfigService;

  @ExceptionHandler(PaymentPlatformException.class)
  protected void handleBusinessException(PaymentPlatformException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (e.getCause() instanceof ServiceException se) {
      var errorResponse = se.getErrorResponse();
      writeResponse(request, response, errorResponse, e);
    } else {
      handle(e, e.getErrorCode(), request, response);
    }
  }

  @ExceptionHandler(Exception.class)
  protected void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
    handle(e, CommonErrorCode.INTERNAL_SERVER_ERROR, request, response);
  }

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  protected void handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
    handle(e, new BusinessErrorCode(CommonErrorCode.INTERNAL_SERVER_ERROR.code(),  "File không được vượt quá 10MB", HttpStatus.INTERNAL_SERVER_ERROR), null, request, response);
  }

  @ExceptionHandler(PartialDataException.class)
  protected void handlePartialDataException(PartialDataException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
    var cause = e.getCause();
    Response<Object> errorResponse;
    if (cause instanceof PaymentPlatformException ppe) {
      errorResponse = Response.ofFailed(ppe.getErrorCode(), ppe.getMessage(), e.getPartialData());
    } else {
      errorResponse = Response.ofFailed(CommonErrorCode.INTERNAL_SERVER_ERROR, e.getMessage(), e.getPartialData());
    }
    writeResponse(request, response, errorResponse, cause);
  }

  @ExceptionHandler(CompletionException.class)
  protected void handleCompletionException(CompletionException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
    var cause = CompletableFutures.unwrapException(e);
    switch (cause) {
      case PaymentPlatformException be -> handleBusinessException(be, request, response);
      case PartialDataException pde -> handlePartialDataException(pde, request, response);
      default -> handle(cause, CommonErrorCode.INTERNAL_SERVER_ERROR, request, response);
    }
  }

  @ExceptionHandler(BindException.class)
  protected void handleBindException(BindException e, HttpServletRequest request,
                                     HttpServletResponse response) throws IOException {
    var fieldViolations = e.getBindingResult().getAllErrors().stream()
        .map(error -> new FieldViolation(((FieldError) error).getField(), error.getDefaultMessage()))
        .toList();

    handleInvalidParams(e, fieldViolations, request, response);
  }

  @ExceptionHandler(ValidateException.class)
  protected void handleValidateException(ValidateException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
    handleInvalidParams(e, e.getFieldViolations(), request, response);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  protected void handleHttpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (e.getCause() instanceof InvalidFormatException ife) {
      var targetType = ife.getTargetType();
      var enumConstants = ife.getTargetType().getEnumConstants();
      var type = enumConstants == null ? targetType.getSimpleName() : targetType.getSimpleName() + Arrays.toString(enumConstants);
      var fieldName = ife.getPath().getLast().getFieldName();
      var description = "Invalid " + fieldName + " require " + type + " type";
      var fieldViolation = new FieldViolation(fieldName, description);
      handleInvalidParams(e, List.of(fieldViolation), request, response);
    } else {
      handle(e, CommonErrorCode.INVALID_PARAMETERS, request, response);
    }
  }

  @ExceptionHandler(ConstraintViolationException.class)
  protected void handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
    var violations = e.getConstraintViolations().stream()
        .map(violation -> {
          var node = ((PathImpl) violation.getPropertyPath()).getLeafNode();
          String filedName;
          if (ElementKind.PARAMETER == node.getKind() || ElementKind.PROPERTY == node.getKind()) {
            filedName = node.getName();
          } else {
            filedName = node.getParent().getName();
          }
          return new FieldViolation(filedName, violation.getMessage());
        })
        .toList();
    handleInvalidParams(e, violations, request, response);
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  protected void handleMissingServletRequestParameterException(MissingServletRequestParameterException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
    var fieldViolations = List.of(new FieldViolation(e.getParameterName(), e.getMessage()));
    handleInvalidParams(e, fieldViolations, request, response);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  protected void handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
    var fieldViolations = List.of(new FieldViolation(e.getName(), e.getMessage()));
    handleInvalidParams(e, fieldViolations, request, response);
  }

  @ExceptionHandler(AccessDeniedException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public void handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
    handle(e, CommonErrorCode.FORBIDDEN, request, response);
  }

  @ExceptionHandler(AuthenticationException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public void handleAuthenticationException(AuthenticationException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
    handle(e, CommonErrorCode.UNAUTHORIZED, request, response);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public void handleDataIntegrityViolationException(DataIntegrityViolationException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
    handle(e, CommonErrorCode.INVALID_PARAMETERS, request, response);
  }

  private void handle(Throwable t, BusinessErrorCode errorCode, HttpServletRequest request, HttpServletResponse response) throws IOException {
    var errorResponse = Response.ofFailed(errorCode, errorCode.message());
    writeResponse(request, response, errorResponse, t);
  }

  private void handle(Throwable t, BusinessErrorCode errorCode, String message, HttpServletRequest request, HttpServletResponse response) throws IOException {
    var errorResponse = Response.ofFailed(errorCode, message);
    writeResponse(request, response, errorResponse, t);
  }

  private void handleInvalidParams(Exception e, List<FieldViolation> fieldViolations, HttpServletRequest request, HttpServletResponse response) throws IOException {
    var errorResponse = Response.ofFailed(CommonErrorCode.INVALID_PARAMETERS, CommonErrorCode.INVALID_PARAMETERS.message(), fieldViolations);
    writeResponse(request, response, errorResponse, e);
  }

  private void writeResponse(HttpServletRequest request, HttpServletResponse servletResponse, Response<?> errorResponse, Throwable t) throws IOException {
    servletResponse.setStatus(errorResponse.getStatus());
    servletResponse.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    var errorMapping = errorMappingConfigService.getByCode(errorResponse.getSoaErrorCode());
    if (errorMapping != null) {
      errorResponse.setSoaErrorDesc(errorMapping.getDescriptionVi());
    }
    byte[] body = objectMapper.writeValueAsBytes(errorResponse);
    LoggingHelper.logResponse(request, servletResponse, errorResponse,
        () -> log.info(() -> SerializationUtils.serializeAsString(objectMapper, new HttpMessage<>(request, servletResponse, errorResponse)), t));
    servletResponse.setContentLength(body.length);
    servletResponse.getOutputStream().write(body);
  }
}