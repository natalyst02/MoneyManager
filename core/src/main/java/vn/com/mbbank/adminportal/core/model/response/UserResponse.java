package vn.com.mbbank.adminportal.core.model.response;

import com.dslplatform.json.JsonReader;
import com.dslplatform.json.runtime.Generics;
import lombok.AllArgsConstructor;
import com.dslplatform.json.CompiledJson;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import vn.com.mbbank.adminportal.common.util.Json;
import vn.com.mbbank.adminportal.core.model.RoleInfo;


import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.List;


@Data
@AllArgsConstructor
@Accessors(chain = true)
@CompiledJson
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String employeeCode;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String jobName;
    private String orgName;
    private List<RoleInfo> roles;
    private String reason;
    private boolean active;
    private OffsetDateTime createdAt;
    private String createdBy;
    private OffsetDateTime updatedAt;
    private String updatedBy;

    public UserResponse(Long id, String username, String employeeCode, String fullName, String phoneNumber, String email, String jobName, String orgName, String roles, String reason, boolean active, OffsetDateTime createdAt, String createdBy, OffsetDateTime updatedAt, String updatedBy) {
        this.id = id;
        this.username = username;
        this.employeeCode = employeeCode;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.jobName = jobName;
        this.orgName = orgName;
        this.roles = roles != null ? Json.decode(roles.getBytes(StandardCharsets.UTF_8), USER_ROLE_READ_OBJECT) : null;
        this.reason = reason;
        this.active = active;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    private static final JsonReader.ReadObject<List<RoleInfo>> USER_ROLE_READ_OBJECT = Json.findReader(Generics.makeParameterizedType(List.class, RoleInfo.class));

}