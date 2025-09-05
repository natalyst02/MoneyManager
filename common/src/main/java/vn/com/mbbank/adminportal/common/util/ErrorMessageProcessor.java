package vn.com.mbbank.adminportal.common.util;


import vn.com.mbbank.adminportal.common.model.entity.ErrorMessage;

public interface ErrorMessageProcessor {
  boolean process(ErrorMessage errorMessage);
}
