package vn.com.mbbank.adminportal.common.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.com.mbbank.adminportal.common.model.filter.ErrorMessageFilter;
import vn.com.mbbank.adminportal.common.model.response.ErrorMessageResponse;

import java.util.List;

public interface ErrorMessageService {
  boolean process(Long id);

  default List<Boolean> process(List<Long> ids) {
    return ids.stream().map(this::process).toList();
  }

  ErrorMessageResponse getErrorMessage(Long id);

  Page<ErrorMessageResponse> getErrorMessages(ErrorMessageFilter filter, Pageable pageable);

  boolean discard(Long id);
}
