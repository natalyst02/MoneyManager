package vn.com.mbbank.adminportal.common.validation;

import org.springframework.security.core.Authentication;
import vn.com.mbbank.adminportal.common.exception.ValidateException;
import vn.com.mbbank.adminportal.common.model.FieldViolation;

import java.util.ArrayList;
import java.util.List;

public interface Validator<T> {
  default void validate(T t) throws ValidateException {
    validate(null, t);
  }

  default void validate(Authentication authentication, T t) throws ValidateException {
    var violations = new ArrayList<FieldViolation>();
    validate(authentication, t, violations);
    if (!violations.isEmpty()) {
      throw new ValidateException("Invalid object: " + t.getClass(), violations);
    }
  }

  default void validate(T t, List<FieldViolation> fieldViolations) {
  }

  default void validate(Authentication authentication, T t, List<FieldViolation> fieldViolations) {
    validate(t, fieldViolations);
  }
}
