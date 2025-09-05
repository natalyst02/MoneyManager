package vn.com.mbbank.adminportal.core.service.internal;

import org.springframework.security.access.prepost.PreAuthorize;
import vn.com.mbbank.adminportal.core.model.RoleInfo;
import vn.com.mbbank.adminportal.core.model.entity.UserRole;
import vn.com.mbbank.adminportal.core.service.UserRoleService;
import java.util.List;


public interface UserRoleServiceInternal extends UserRoleService {
    @PreAuthorize("hasPermission('role', T(vn.com.mbbank.adminportal.core.model.BitmaskValue).ASSIGN)")
    void updateUserRoles(String updatedBy, Long userId, List<Long> roleIds);
    void createUserRoles(String createdBy, Long userId, List<Long> roleIds);
    List<RoleInfo> findRolesByUserId(Long userId);
    void removeUserRoles(Long userId);
    List<UserRole> delete(List<Long>userRoleIds);
}
