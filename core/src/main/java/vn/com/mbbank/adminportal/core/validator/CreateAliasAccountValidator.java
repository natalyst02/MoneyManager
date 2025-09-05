package vn.com.mbbank.adminportal.core.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.com.mbbank.adminportal.common.model.FieldViolation;
import vn.com.mbbank.adminportal.common.validation.Validator;
import vn.com.mbbank.adminportal.core.model.PartnerType;
import vn.com.mbbank.adminportal.core.model.request.CreateAliasAccountRequest;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@RequiredArgsConstructor
@Component
public class CreateAliasAccountValidator implements Validator<CreateAliasAccountRequest> {

  @Override
  public void validate(CreateAliasAccountRequest createAliasAccountRequest, List<FieldViolation> fieldViolations) {
    try {
      Pattern.compile(createAliasAccountRequest.getRegex());
    } catch (PatternSyntaxException e) {
      fieldViolations.add(new FieldViolation("regex", "Invalid regex value!"));
    }
    Long minTransLimit = createAliasAccountRequest.getMinTransLimit();
    if(minTransLimit != null && minTransLimit < 0) {
      fieldViolations.add(new FieldViolation("minTransLimit", "minTransLimit require positive!"));
    }
    Long maxTransLimit = createAliasAccountRequest.getMaxTransLimit();
    if(maxTransLimit != null && maxTransLimit < 0) {
      fieldViolations.add(new FieldViolation("maxTransLimit", "maxTransLimit require positive!"));
    }
    if(minTransLimit != null && maxTransLimit != null
    && minTransLimit > maxTransLimit) {
      fieldViolations.add(new FieldViolation("minTransLimit", "minTransLimit require less than maxTransLimit!"));
    }
    if (PartnerType.NORMAL_PARTNER == createAliasAccountRequest.getPartnerType()) {
      if (createAliasAccountRequest.getPartnerAccount() == null || createAliasAccountRequest.getPartnerAccount().isBlank()) {
        fieldViolations.add(new FieldViolation("partnerAccount", "partnerAccount is required when partnerType is NORMAL_PARTNER"));
      }
      if (createAliasAccountRequest.getGetNameUrl() == null || createAliasAccountRequest.getGetNameUrl().isBlank()) {
        fieldViolations.add(new FieldViolation("getNameUrl", "getNameUrl is required when partnerType is NORMAL_PARTNER"));
      }
      if (createAliasAccountRequest.getConfirmUrl() == null || createAliasAccountRequest.getConfirmUrl().isBlank()) {
        fieldViolations.add(new FieldViolation("confirmUrl", "confirmUrl is required when partnerType is NORMAL_PARTNER"));
      }
      if (createAliasAccountRequest.getProtocol() == null || createAliasAccountRequest.getProtocol().isBlank()) {
        fieldViolations.add(new FieldViolation("protocol", "protocol is required when partnerType is NORMAL_PARTNER"));
      }
    }
  }
}
