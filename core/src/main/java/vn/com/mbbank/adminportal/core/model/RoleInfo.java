package vn.com.mbbank.adminportal.core.model;

import com.dslplatform.json.CompiledJson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@CompiledJson
@Data
@Accessors(chain = true)
@AllArgsConstructor
public class RoleInfo{
    private Long roleId;
    private String roleCode;
    private String roleName;
    private RoleType roleType;
}