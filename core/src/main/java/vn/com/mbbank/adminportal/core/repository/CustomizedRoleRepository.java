package vn.com.mbbank.adminportal.core.repository;

import vn.com.mbbank.adminportal.core.model.entity.Role;

import java.util.Optional;

public interface CustomizedRoleRepository {
  Optional<Role> updateRole(Role role);
}
