package vn.com.mbbank.adminportal.common.exception;

import org.springframework.http.HttpStatus;

public record BusinessErrorCode(String code, String message, HttpStatus httpStatus) {
  public BusinessErrorCode(int code, String message, HttpStatus httpStatus) {
    this(String.valueOf(code), message, httpStatus);
  }
}

