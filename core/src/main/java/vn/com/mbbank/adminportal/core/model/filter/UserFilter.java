package vn.com.mbbank.adminportal.core.model.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@AllArgsConstructor
@Accessors(chain = true)
public class UserFilter {
    private String roleCode;
    private String roleName;
    private String username;
    private String fullName;
    private String phoneNumber;
    private Boolean active;
}