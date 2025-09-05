package vn.com.mbbank.adminportal.core.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Accessors(chain = true)
public class UpdateRoleRequest {
  @NotBlank
  @Size(max = 250)
  private String name;
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
