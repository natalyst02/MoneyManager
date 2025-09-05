package vn.com.mbbank.adminportal.core.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.com.mbbank.adminportal.common.model.response.Response;
import vn.com.mbbank.adminportal.core.model.response.PermissionResponse;
import vn.com.mbbank.adminportal.core.service.PermissionService;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/permissions")
public class PermissionController {
  private final PermissionService permissionService;

  @GetMapping
  public Response<List<PermissionResponse>> getAllPermission() {
    return Response.ofSucceeded(permissionService.getAll());
  }
}