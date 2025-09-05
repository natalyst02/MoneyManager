package vn.com.mbbank.adminportal.common.util;

import org.springframework.data.jpa.domain.Specification;
import vn.com.mbbank.adminportal.common.model.entity.ErrorMessage;
import vn.com.mbbank.adminportal.common.model.entity.ErrorMessage_;
import vn.com.mbbank.adminportal.common.model.filter.ErrorMessageFilter;

import static vn.com.mbbank.adminportal.common.util.Specifications.*;
import static vn.com.mbbank.adminportal.common.util.Specifications.contain;
import static vn.com.mbbank.adminportal.common.util.Specifications.in;
import static vn.com.mbbank.adminportal.common.util.Specifications.lessThan;

public class Filters {
  public static Specification<ErrorMessage> toSpecification(ErrorMessageFilter filter) {
    return Specification.where(contain(ErrorMessage_.message, filter.getMessageText()))
        .and(contain(ErrorMessage_.error, filter.getErrorText()))
        .and(in(ErrorMessage_.topic, filter.getTopics()))
        .and(in(ErrorMessage_.key, filter.getKeys()))
        .and(in(ErrorMessage_.status, filter.getStatuses()))
        .and(greaterThanOrEqualTo(ErrorMessage_.createdAt, filter.getCreatedAtFrom()))
        .and(lessThan(ErrorMessage_.createdAt, filter.getCreatedAtTo()));
  }

  private Filters() {
    throw new UnsupportedOperationException();
  }
}
