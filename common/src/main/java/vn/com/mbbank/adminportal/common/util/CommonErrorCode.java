package vn.com.mbbank.adminportal.common.util;

import org.springframework.http.HttpStatus;
import vn.com.mbbank.adminportal.common.exception.BusinessErrorCode;

public interface CommonErrorCode {
  BusinessErrorCode INTERNAL_SERVER_ERROR =
      new BusinessErrorCode(5000, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
  BusinessErrorCode INTERNAL_SERVICE_ERROR =
      new BusinessErrorCode(5001, "Internal service error", HttpStatus.INTERNAL_SERVER_ERROR);
  BusinessErrorCode INTERNAL_DATABASE_ERROR =
      new BusinessErrorCode(5002, "Internal database error", HttpStatus.INTERNAL_SERVER_ERROR);
  BusinessErrorCode GET_OAUTH_TOKEN_TIMEOUT =
      new BusinessErrorCode(5003, "Get oauth token timeout", HttpStatus.INTERNAL_SERVER_ERROR);
  BusinessErrorCode GET_OAUTH_TOKEN_ERROR =
      new BusinessErrorCode(5004, "Get oauth token error", HttpStatus.INTERNAL_SERVER_ERROR);
  BusinessErrorCode INVALID_PARAMETERS =
      new BusinessErrorCode(4000, "Invalid parameters", HttpStatus.BAD_REQUEST);
  BusinessErrorCode UNAUTHORIZED =
      new BusinessErrorCode(4001, "You need to login to to access this resource", HttpStatus.UNAUTHORIZED);
  BusinessErrorCode FORBIDDEN =
      new BusinessErrorCode(4002, "You don't have permission to to access this resource", HttpStatus.FORBIDDEN);
  BusinessErrorCode ERROR_MESSAGE_NOT_FOUND =
      new BusinessErrorCode(4003, "Error message not found", HttpStatus.NOT_FOUND);
  BusinessErrorCode EVENT_MESSAGE_NOT_FOUND =
      new BusinessErrorCode(4004, "Event message not found", HttpStatus.NOT_FOUND);
}
