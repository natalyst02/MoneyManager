package vn.com.mbbank.adminportal.common.validation;


import vn.com.mbbank.adminportal.common.exception.ValidateException;
import vn.com.mbbank.adminportal.common.model.FieldViolation;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SequencedCollection;
import java.util.function.BiConsumer;

public class Validators {
  private Validators() {
    throw new UnsupportedOperationException();
  }

  public static void requireNull(String fieldName, Object value, List<FieldViolation> fieldViolations) {
    if (value != null) {
      fieldViolations.add(new FieldViolation(fieldName, fieldName + " must be null"));
    }
  }

  public static void requireNonNull(String fieldName, Object value, List<FieldViolation> fieldViolations) throws ValidateException {
    if (value == null) {
      fieldViolations.add(new FieldViolation(fieldName, fieldName + " must be non null"));
    }
  }

  public static void requireNonEmpty(String fieldName, String value, List<FieldViolation> fieldViolations) throws ValidateException {
    if (value == null || value.isEmpty()) {
      fieldViolations.add(new FieldViolation(fieldName, fieldName + " must be non empty"));
    }
  }

  public static void requireNonEmpty(String fieldName, Collection<?> value, List<FieldViolation> fieldViolations) throws ValidateException {
    if (value == null || value.isEmpty()) {
      fieldViolations.add(new FieldViolation(fieldName, fieldName + " must be non empty collection"));
    }
  }

  public static void requireNonEmpty(String fieldName, Map<?, ?> value, List<FieldViolation> fieldViolations) throws ValidateException {
    if (value == null || value.isEmpty()) {
      fieldViolations.add(new FieldViolation(fieldName, fieldName + " must be non empty map"));
    }
  }

  public static void requireNotBlank(String fieldName, String value, List<FieldViolation> fieldViolations) throws ValidateException {
    if (value == null || value.isBlank()) {
      fieldViolations.add(new FieldViolation(fieldName, fieldName + " must be not blank"));
    }
  }

  public static void requireUnsigned(String fieldName, Integer value, List<FieldViolation> fieldViolations) throws ValidateException {
    if (value == null || value < 0) {
      fieldViolations.add(new FieldViolation(fieldName, fieldName + " must be non negative"));
    }
  }

  public static void requireUnsigned(String fieldName, Long value, List<FieldViolation> fieldViolations) throws ValidateException {
    if (value == null || value < 0) {
      fieldViolations.add(new FieldViolation(fieldName, fieldName + " must be non negative"));
    }
  }

  public static void requireUnsigned(String fieldName, Float value, List<FieldViolation> fieldViolations) throws ValidateException {
    if (value == null || value < 0) {
      fieldViolations.add(new FieldViolation(fieldName, fieldName + " must be non negative"));
    }
  }

  public static void requireUnsigned(String fieldName, Double value, List<FieldViolation> fieldViolations) throws ValidateException {
    if (value == null || value < 0) {
      fieldViolations.add(new FieldViolation(fieldName, fieldName + " must be non negative"));
    }
  }

  public static void requirePositive(String fieldName, Integer value, List<FieldViolation> fieldViolations) throws ValidateException {
    if (value == null || value <= 0) {
      fieldViolations.add(new FieldViolation(fieldName, fieldName + " must be positive"));
    }
  }

  public static void requirePositive(String fieldName, Long value, List<FieldViolation> fieldViolations) throws ValidateException {
    if (value == null || value <= 0) {
      fieldViolations.add(new FieldViolation(fieldName, fieldName + " must be positive"));
    }
  }

  public static void requirePositive(String fieldName, long value, List<FieldViolation> fieldViolations) throws ValidateException {
    if (value <= 0) {
      fieldViolations.add(new FieldViolation(fieldName, fieldName + " must be positive"));
    }
  }

  public static void requireUnsigned(String fieldName, long value, List<FieldViolation> fieldViolations) throws ValidateException {
    if (value < 0) {
      fieldViolations.add(new FieldViolation(fieldName, fieldName + " must be positive"));
    }
  }

  public static void requirePositive(String fieldName, BigInteger value, List<FieldViolation> fieldViolations) throws ValidateException {
    if (value == null || value.compareTo(BigInteger.ZERO) <= 0) {
      fieldViolations.add(new FieldViolation(fieldName, fieldName + " must be positive"));
    }
  }

  public static void requirePositive(String fieldName, BigDecimal value, List<FieldViolation> fieldViolations) throws ValidateException {
    if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
      fieldViolations.add(new FieldViolation(fieldName, fieldName + " must be positive"));
    }
  }

  public static <T> void requireEquals(String fieldName, T value, T expect, List<FieldViolation> fieldViolations) throws ValidateException {
    if (value == null || !value.equals(expect)) {
      fieldViolations.add(new FieldViolation(fieldName, fieldName + " must be " + expect));
    }
  }

  public static <T extends Comparable<T>> void requireGreaterThan(String fieldName, List<T> values, T number, List<FieldViolation> fieldViolations) throws ValidateException {
    if (values == null || values.isEmpty()) {
      fieldViolations.add(new FieldViolation(fieldName, fieldName + " must contains number > " + number));
      return;
    }
    for (var value : values) {
      if (value == null || value.compareTo(number) <= 0) {
        fieldViolations.add(new FieldViolation(fieldName, fieldName + " must contains number > " + number));
      }
    }
  }

  public static <T extends Comparable<T>> void requireGreaterThanOrEquals(String fieldName, List<T> values, T number, List<FieldViolation> fieldViolations) throws ValidateException {
    if (values == null || values.isEmpty()) {
      fieldViolations.add(new FieldViolation(fieldName, fieldName + " must contains number >= " + number));
      return;
    }
    for (var value : values) {
      if (value == null || value.compareTo(number) < 0) {
        fieldViolations.add(new FieldViolation(fieldName, fieldName + " must contains number >= " + number));
      }
    }
  }

  public static <T extends Comparable<T>> void requireRange(String fieldName, List<T> values, T from, T to, List<FieldViolation> fieldViolations) throws ValidateException {
    if (values == null || values.isEmpty()) {
      fieldViolations.add(new FieldViolation(fieldName, fieldName + " must contains number in range [ " + from + "," + to + ")"));
      return;
    }
    for (var value : values) {
      if (value == null || value.compareTo(from) < 0 || value.compareTo(to) >= 0) {
        fieldViolations.add(new FieldViolation(fieldName, fieldName + " must contains number in range [ " + from + "," + to + ")"));
      }
    }
  }

  public static void optionPositive(String fieldName, Long value, List<FieldViolation> fieldViolations) throws ValidateException {
    if (value != null && value <= 0) {
      fieldViolations.add(new FieldViolation(fieldName, fieldName + " must be positive"));
    }
  }

  public static void requireDateFromDateTo(String fieldDateFrom, String fieldDateTo, Long dateFrom, Long dateTo, List<FieldViolation> fieldViolations) throws ValidateException {
    if (dateFrom != null && dateTo != null) {
      if (dateFrom.compareTo(dateTo) >= 0) {
        fieldViolations.add(new FieldViolation(fieldDateTo, fieldDateTo + " must be greater than " + fieldDateFrom));
      }
    }
  }

  public static void requireValueBetweenConst(String fieldName, Integer value, int start, int end, List<FieldViolation> fieldViolations) throws ValidateException {
    if (value == null || value < start || value > end) {
      fieldViolations.add(new FieldViolation(fieldName, fieldName + " must greater than " + start + " and less than " + end));
    }
  }

  public static void requireValueBetweenValue(String fieldName1, String fieldName2, Integer value1, Integer value2, List<FieldViolation> fieldViolations) throws ValidateException {
    if (value1 == null || value2 == null || value2 <= value1) {
      fieldViolations.add(new FieldViolation(fieldName1, fieldName2 + " must be greater than " + fieldName1));
    }
  }

  public static void requireIntegerValue(String fieldName, Float value, List<FieldViolation> fieldViolations) throws ValidateException {
    if (value == null || value.intValue() != value) {
      fieldViolations.add(new FieldViolation(fieldName, fieldName + " must be integer"));
    }
  }

  public static void requireOnlyNonEmpty(String fieldName1, String value1, String fieldName2, String value2, List<FieldViolation> fieldViolations) throws ValidateException {
    if ((value1 == null || value1.isEmpty()) && (value2 == null || value2.isEmpty())) {
      fieldViolations.add(new FieldViolation(fieldName1, fieldName1 + " or " + fieldName2 + " must be non empty"));
      fieldViolations.add(new FieldViolation(fieldName2, fieldName1 + " or " + fieldName2 + " must be non empty"));
    }
  }

  public static void requireLength(String fieldName, String value, int length, List<FieldViolation> fieldViolations) throws ValidateException {
    if (value == null || value.length() != length) {
      fieldViolations.add(new FieldViolation(fieldName, fieldName + " must has length = " + length));
    }
  }

  public static <T> void validateElement(String fieldName, SequencedCollection<T> values, List<FieldViolation> fieldViolations, Validator<T> validator) {
    validateElement(fieldName, values, fieldViolations, validator::validate);
  }

  public static <T> void validateElement(String fieldName, SequencedCollection<T> values, List<FieldViolation> fieldViolations, BiConsumer<T, List<FieldViolation>> validator) {
    int i = 0;
    var subFieldViolations = new ArrayList<FieldViolation>();
    for (var v : values) {
      validator.accept(v, subFieldViolations);
      for (var fieldViolation : subFieldViolations) {
        fieldViolations.add(fieldViolation.withField(fieldName + "[" + i + "]." + fieldViolation.getField()));
      }
      subFieldViolations.clear();
      ++i;
    }
  }
}