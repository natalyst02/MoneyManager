package vn.com.mbbank.adminportal.core.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.com.mbbank.adminportal.common.model.response.PageImpl;
import vn.com.mbbank.adminportal.common.model.response.Response;
import vn.com.mbbank.adminportal.common.util.Pageables;
import vn.com.mbbank.adminportal.core.model.request.CreateRoleRequest;
import vn.com.mbbank.adminportal.core.model.request.UpdateRoleRequest;
import vn.com.mbbank.adminportal.core.model.response.CreateRoleResponse;
import vn.com.mbbank.adminportal.core.model.response.GetRoleResponse;
import vn.com.mbbank.adminportal.core.model.response.RoleResponse;
import vn.com.mbbank.adminportal.core.model.filter.RoleFilter;
import vn.com.mbbank.adminportal.core.model.response.UpdateRoleResponse;
import vn.com.mbbank.adminportal.core.service.internal.RoleServiceInternal;


@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@Validated
public class RoleController {
  private final RoleServiceInternal roleService;

  @GetMapping
  public Response<PageImpl<RoleResponse>> getRoles(@Valid RoleFilter request, @RequestParam int page, @RequestParam int size,
                                                   @RequestParam(required = false, defaultValue = "id:ASC") String sort) {
    return Response.ofSucceeded(roleService.getRoles(request, Pageables.of(page, size, sort)));
  }

  @GetMapping("/inquiry/{id}")
  public Response<GetRoleResponse> getRole(@PathVariable @Positive Long id) {
    return Response.ofSucceeded(roleService.get(id));
  }

  @PostMapping
  public Response<CreateRoleResponse> createRole(Authentication authentication, @Valid @RequestBody CreateRoleRequest request) {
    return Response.ofSucceeded(roleService.create(authentication, request));
  }

  @PutMapping("/{id}")
  public Response<UpdateRoleResponse> updateRole(Authentication authentication, @PathVariable @Positive Long id, @Valid @RequestBody UpdateRoleRequest request) {
    return Response.ofSucceeded(roleService.update(authentication, id, request));
  }
}
