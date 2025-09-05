package vn.com.mbbank.adminportal.core.repository;

import vn.com.mbbank.adminportal.core.model.entity.UserRole;

import java.util.List;
import java.util.Optional;

public interface CustomizedUserRoleRepository {
  Optional<List<UserRole>> deleteUserRole(List<Long> userRoleIds);
}
