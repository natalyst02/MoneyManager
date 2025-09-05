package vn.com.mbbank.adminportal.core.service;

import vn.com.mbbank.adminportal.core.model.response.PermissionResponse;

import java.util.List;

public interface PermissionService {
  List<PermissionResponse> getAll();
}
