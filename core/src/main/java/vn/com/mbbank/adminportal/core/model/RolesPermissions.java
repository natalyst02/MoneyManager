package vn.com.mbbank.adminportal.core.model;

import java.util.Map;

public interface RolesPermissions {
  Long getRoleId();
  Map<String, Integer> getPermissions();
}
