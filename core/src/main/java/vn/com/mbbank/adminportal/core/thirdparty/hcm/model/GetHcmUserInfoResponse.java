package vn.com.mbbank.adminportal.core.thirdparty.hcm.model;

import com.dslplatform.json.CompiledJson;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@CompiledJson
public class GetHcmUserInfoResponse {
    private String employeeCode;
    private String fullName;
    private String mobileNumber;
    private String email;
    private String orgCodeManage;
    private String orgCodeLevel1;
    private String orgCodeLevel2;
    private String orgCodeLevel3;
    private String orgNameManage;
    private String orgNameLevel1;
    private String orgNameLevel2;
    private String orgNameLevel3;
    private String jobCode;
    private String jobName;
}
