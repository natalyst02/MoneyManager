package vn.com.mbbank.adminportal.core.model.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Accessors(chain = true)
public class UpdateUserRequest {
    private Boolean active;
    @Size(max = 2000)
    @NotBlank
    private String reason;
    @Valid
    private List<@NonNull Long> roleIds;
    @AssertTrue(message = "Either 'active' or 'roleIds' must not be null")
    private boolean isValidActiveOrRoleIds() {
        return active != null || (roleIds != null);
    }
}