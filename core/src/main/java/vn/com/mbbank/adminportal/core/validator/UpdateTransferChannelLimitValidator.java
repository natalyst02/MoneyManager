package vn.com.mbbank.adminportal.core.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.com.mbbank.adminportal.common.model.FieldViolation;
import vn.com.mbbank.adminportal.common.validation.Validator;
import vn.com.mbbank.adminportal.core.model.TransferChannel;
import vn.com.mbbank.adminportal.core.model.request.UpdateTransferChannelLimitRequest;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Component
public class UpdateTransferChannelLimitValidator implements Validator<UpdateTransferChannelLimitRequest> {
    @Override
    public void validate(UpdateTransferChannelLimitRequest createRequest, List<FieldViolation> fieldViolations) {
        BigDecimal minAmount = createRequest.getMinAmount();
        BigDecimal maxAmount = createRequest.getMaxAmount();
        if(minAmount != null && maxAmount != null
                && minAmount.compareTo(maxAmount) >= 0) {
            fieldViolations.add(new FieldViolation("minAmount", "minAmount require less than maxAmount!"));
        }
        if (minAmount != null && minAmount.scale() > 0) {
            fieldViolations.add(new FieldViolation("minAmount", "minAmount must be an integer"));
        }

        if (maxAmount != null && maxAmount.scale() > 0) {
            fieldViolations.add(new FieldViolation("maxAmount", "maxAmount must be an integer"));
        }
        BigDecimal fragmentAmount = createRequest.getFragmentAmount();
        BigDecimal fragmentMaxAmount = createRequest.getFragmentMaxAmount();

        if (fragmentAmount != null) {
            if(!TransferChannel.getAllowedTransferChannelLimits().contains(createRequest.getTransferChannel())) {
                fieldViolations.add(new FieldViolation("fragmentAmount", "transferChannel is not allowed for this field"));
            } else {
                if(fragmentMaxAmount == null) {
                    fieldViolations.add(new FieldViolation("fragmentMaxAmount", "fragmentMaxAmount is required"));
                }
                if(fragmentAmount.scale() > 0) {
                    fieldViolations.add(new FieldViolation("fragmentAmount", "fragmentAmount must be an integer"));
                }
                if(fragmentAmount.compareTo(minAmount) <= 0) {
                    fieldViolations.add(new FieldViolation("fragmentAmount", "fragmentAmount require greater than minAmount"));
                }
                if(fragmentAmount.compareTo(maxAmount) >= 0) {
                    fieldViolations.add(new FieldViolation("fragmentAmount", "fragmentAmount require less than maxAmount"));
                }
                BigDecimal sumFragmentAndMin = fragmentAmount.add(minAmount);
                if (sumFragmentAndMin.compareTo(maxAmount) >= 0) {
                    fieldViolations.add(new FieldViolation("maxAmount", "maxAmount must be greater than the sum of fragmentAmount and minAmount!"));
                }
            }
        }
        if (fragmentMaxAmount != null) {
            if(!TransferChannel.getAllowedTransferChannelLimits().contains(createRequest.getTransferChannel())) {
                fieldViolations.add(new FieldViolation("fragmentMaxAmount", "transferChannel is not allowed for this field"));
            } else {
                if(fragmentMaxAmount.scale() > 0) {
                    fieldViolations.add(new FieldViolation("fragmentMaxAmount", "fragmentMaxAmount must be an integer"));
                }
                if(fragmentAmount == null) {
                    fieldViolations.add(new FieldViolation("fragmentAmount", "fragmentAmount is required"));
                }
                if(fragmentAmount != null && fragmentMaxAmount.compareTo(fragmentAmount) <= 0) {
                    fieldViolations.add(new FieldViolation("fragmentMaxAmount", "fragmentMaxAmount require greater than fragmentAmount!"));
                }
                if(fragmentMaxAmount.compareTo(maxAmount) <= 0) {
                    fieldViolations.add(new FieldViolation("fragmentMaxAmount", "fragmentMaxAmount require greater than maxAmount"));
                }
            }
        }
    }
}
