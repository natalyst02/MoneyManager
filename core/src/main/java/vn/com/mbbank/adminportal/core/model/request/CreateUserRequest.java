package vn.com.mbbank.adminportal.core.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Accessors(chain = true)
public class CreateUserRequest {
    @NotBlank
    @Size(max = 50)
    private String username;
    private List<@NotNull Long> roleIds;
    private Boolean active;
    @Size(max = 2000)
    private String reason;
}
