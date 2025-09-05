package vn.com.mbbank.adminportal.core.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.com.mbbank.adminportal.common.model.FieldViolation;
import vn.com.mbbank.adminportal.common.validation.Validator;
import vn.com.mbbank.adminportal.core.model.PartnerType;
import vn.com.mbbank.adminportal.core.model.request.UpdateAliasAccountRequest;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@RequiredArgsConstructor
@Component
public class UpdateAliasAccountValidator implements Validator<UpdateAliasAccountRequest> {

  @Override
  public void validate(UpdateAliasAccountRequest updateAliasAccountRequest, List<FieldViolation> fieldViolations) {
    try {
      Pattern.compile(updateAliasAccountRequest.getRegex());
    } catch (PatternSyntaxException e) {
      fieldViolations.add(new FieldViolation("regex", "Invalid regex value!"));
    }
    Long minTransLimit = updateAliasAccountRequest.getMinTransLimit();
    if(minTransLimit != null && minTransLimit < 0) {
      fieldViolations.add(new FieldViolation("minTransLimit", "minTransLimit require positive!"));
    }
    Long maxTransLimit = updateAliasAccountRequest.getMaxTransLimit();
    if(maxTransLimit != null && maxTransLimit < 0) {
      fieldViolations.add(new FieldViolation("maxTransLimit", "maxTransLimit require positive!"));
    }
    if (minTransLimit != null && maxTransLimit != null
        && minTransLimit > maxTransLimit) {
      fieldViolations.add(new FieldViolation("minTransLimit", "minTransLimit require less than maxTransLimit!"));
    }
    if (PartnerType.NORMAL_PARTNER == updateAliasAccountRequest.getPartnerType()) {
      if (updateAliasAccountRequest.getPartnerAccount() == null || updateAliasAccountRequest.getPartnerAccount().isBlank()) {
        fieldViolations.add(new FieldViolation("partnerAccount", "partnerAccount is required when partnerType is NORMAL_PARTNER"));
      }
      if (updateAliasAccountRequest.getGetNameUrl() == null || updateAliasAccountRequest.getGetNameUrl().isBlank()) {
        fieldViolations.add(new FieldViolation("getNameUrl", "getNameUrl is required when partnerType is NORMAL_PARTNER"));
      }
      if (updateAliasAccountRequest.getConfirmUrl() == null || updateAliasAccountRequest.getConfirmUrl().isBlank()) {
        fieldViolations.add(new FieldViolation("confirmUrl", "confirmUrl is required when partnerType is NORMAL_PARTNER"));
      }
      if (updateAliasAccountRequest.getProtocol() == null || updateAliasAccountRequest.getProtocol().isBlank()) {
        fieldViolations.add(new FieldViolation("protocol", "protocol is required when partnerType is NORMAL_PARTNER"));
      }
    }
  }
}
