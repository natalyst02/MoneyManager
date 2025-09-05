package vn.com.mbbank.adminportal.core.service.internal;

import vn.com.mbbank.adminportal.core.model.entity.User;
import vn.com.mbbank.adminportal.core.service.UserService;

import java.util.List;

public interface UserServiceInternal extends UserService {
  User getActiveUserByUsername(String username);

  List<String> getUsernamesByRoleId(Long roleID);
}
