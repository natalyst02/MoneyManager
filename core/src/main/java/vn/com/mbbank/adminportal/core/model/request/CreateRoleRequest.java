package vn.com.mbbank.adminportal.core.model.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import vn.com.mbbank.adminportal.core.model.RoleType;

import java.util.List;

@Getter
@Accessors(chain = true)
public class CreateRoleRequest {
  @NotBlank
  @Size(max = 50)
  @Pattern(regexp = "^[a-zA-Z0-9-_]+$")
  private String code;
  @NotBlank
  @Size(max = 250)
  private String name;
  @NotNull
  private RoleType type;
  @Size(max = 2000)
  private String description;
  @NotNull
  private Boolean active;
  @NotEmpty
  private List<Long> permissionIds;
  @NotEmpty
  @Size(max = 2000)
  private String reason;
}
