package vn.com.mbbank.adminportal.core.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import vn.com.mbbank.adminportal.core.model.request.CreateRoleRequest;
import vn.com.mbbank.adminportal.core.model.filter.RoleFilter;
import vn.com.mbbank.adminportal.core.model.request.UpdateRoleRequest;
import vn.com.mbbank.adminportal.core.model.response.CreateRoleResponse;
import vn.com.mbbank.adminportal.core.model.response.GetRoleResponse;
import vn.com.mbbank.adminportal.core.model.response.RoleResponse;
import vn.com.mbbank.adminportal.core.model.response.UpdateRoleResponse;

public interface RoleService {
  Page<RoleResponse> getRoles(RoleFilter request, Pageable pageable);
  CreateRoleResponse create(Authentication authentication, CreateRoleRequest request);
  UpdateRoleResponse update(Authentication authentication, Long id, UpdateRoleRequest request);
  GetRoleResponse get(Long id);
}
