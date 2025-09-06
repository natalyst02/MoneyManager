package vn.com.mbbank.adminportal.core.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.com.mbbank.adminportal.common.model.FieldViolation;
import vn.com.mbbank.adminportal.common.validation.Validator;
import vn.com.mbbank.adminportal.core.model.request.FileInput;
import vn.com.mbbank.adminportal.core.model.request.UpdateAliasAccountRequest;

import java.util.List;

@RequiredArgsConstructor
@Component
public class FileInputValidator implements Validator<FileInput> {
    @Override
    public void validate(FileInput input, List<FieldViolation> fieldViolations) {
        if (input.getPage() < 1) {
            fieldViolations.add(new FieldViolation("Page", "Page must be greater than 0!"));
        }
        if (input.getPageSize() < 1) {
            fieldViolations.add(new FieldViolation("PageSize", "Page Size must be greater than 0!"));
        }
        if (input.getFromDate() != null && !input.getFromDate().matches("\\d{4}-\\d{2}-\\d{2}")) {
            fieldViolations.add(new FieldViolation("FromDate", "fromDate must be in yyyy-MM-dd format"));
        }
        if (input.getToDate() != null && !input.getToDate().matches("\\d{4}-\\d{2}-\\d{2}")) {
            fieldViolations.add(new FieldViolation("ToDate", "ToDate must be in yyyy-MM-dd format"));
        }

    }
}
