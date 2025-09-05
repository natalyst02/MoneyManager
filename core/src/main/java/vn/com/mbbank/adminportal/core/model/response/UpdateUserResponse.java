package vn.com.mbbank.adminportal.core.model.response;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Accessors(chain = true)
public class UpdateUserResponse {
    private Long id;
    private String username;
    private String employeeCode;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String jobName;
    private String orgName;
    private String reason;
    private boolean active;
    private OffsetDateTime createdAt;
    private String createdBy;
    private OffsetDateTime updatedAt;
    private String updatedBy;
}