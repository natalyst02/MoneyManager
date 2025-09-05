package vn.com.mbbank.adminportal.core.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.com.mbbank.adminportal.common.model.FieldViolation;
import vn.com.mbbank.adminportal.common.validation.Validator;
import vn.com.mbbank.adminportal.core.model.request.UpdateTransferChannelConfigPrioritiesRequest;
import vn.com.mbbank.adminportal.core.util.StringUtils;

import java.util.HashMap;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UpdateTransferChannelConfigPrioritiesValidator implements Validator<UpdateTransferChannelConfigPrioritiesRequest> {
  @Override
  public void validate(UpdateTransferChannelConfigPrioritiesRequest request, List<FieldViolation> fieldViolations) {
    var priorityReferences = request.getPriorities();
    var reason = request.getReason();
    if (priorityReferences == null || priorityReferences.isEmpty()) {
      fieldViolations.add(new FieldViolation("priorities", "must not be null or empty"));
    } else {
      var checkIds = new HashMap<Long, Integer>();
      var checkPriorities = new HashMap<Integer, Integer>();
      for (var i = 0; i < priorityReferences.size(); i++) {
        if (priorityReferences.get(i) == null) {
          fieldViolations.add(new FieldViolation("priorities[" + i + "]", "must not be null"));
        } else {
          var id = priorityReferences.get(i).getId();
          var priority = priorityReferences.get(i).getPriority();
          if (!checkIds.containsKey(id)) {
            checkIds.put(id, 1);
          } else {
            var countId = checkIds.get(id);
            checkIds.put(id, ++countId);
          }
          if (!checkPriorities.containsKey(priority)) {
            checkPriorities.put(priority, 1);
          } else {
            var countPriority = checkPriorities.get(priority);
            checkPriorities.put(priority, ++countPriority);
          }
          if (id == null) {
            fieldViolations.add(new FieldViolation("priorities[" + i + "].id", "must not be null"));
          }
          if (priority == null) {
            fieldViolations.add(new FieldViolation("priorities[" + i + "].priority", "must not be null"));
          } else if (priority <= 0) {
            fieldViolations.add(new FieldViolation("priorities[" + i + "].priority", "must be greater than or equal to 1"));
          }
        }
      }
      checkIds.forEach((id, count) -> {
        if (count > 1) {
          fieldViolations.add(new FieldViolation("id", "priorities must not contain duplicate id: " + id));
        }
      });
      checkPriorities.forEach((priority, count) -> {
        if (count > 1) {
          fieldViolations.add(new FieldViolation("priority", "priorities must not contain duplicate priority: " + priority));
        }
      });
      if (StringUtils.isEmpty(reason)) {
        fieldViolations.add(new FieldViolation("reason", "must not be blank"));
      } else if (reason.length() > 2000) {
        fieldViolations.add(new FieldViolation("reason", "length can not over 2000"));
      }
    }
  }
}
