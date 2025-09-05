package vn.com.mbbank.adminportal.core.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Data
@Accessors(chain = true)
public class LoginRequest {
    @NotNull
    @Length(max = 20)
    private String username;
    @NotNull
    @Length(max = 20)
    private String password;
}
