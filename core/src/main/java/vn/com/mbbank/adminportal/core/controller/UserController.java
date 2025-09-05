package vn.com.mbbank.adminportal.core.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.com.mbbank.adminportal.common.model.response.PageImpl;
import vn.com.mbbank.adminportal.common.model.response.Response;
import vn.com.mbbank.adminportal.common.util.Pageables;
import vn.com.mbbank.adminportal.core.model.request.CreateUserRequest;
import vn.com.mbbank.adminportal.core.model.request.UpdateUserRequest;
import vn.com.mbbank.adminportal.core.model.filter.UserFilter;
import vn.com.mbbank.adminportal.core.model.response.UserPermissionsResp;
import vn.com.mbbank.adminportal.core.model.response.UserResponse;
import vn.com.mbbank.adminportal.core.service.UserService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import vn.com.mbbank.adminportal.core.thirdparty.hcm.model.GetHcmUserInfoResponse;

@Log4j2
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasPermission('user', T(vn.com.mbbank.adminportal.core.model.BitmaskValue).VIEW)")
    @GetMapping("/hcm/{username}")
    public CompletableFuture<Response<GetHcmUserInfoResponse>> getHcmUser(Authentication authentication,
                                                                       @Valid @NotBlank @PathVariable
                                                                          @Size(max = 50, message = "Username max length is 50 characters")
                                                                          String username) {
        return userService.getHcmUser(authentication, username).thenApply(Response::ofSucceeded);
    }

    @PreAuthorize("hasPermission('user', T(vn.com.mbbank.adminportal.core.model.BitmaskValue).UPDATE)")
    @PutMapping("/{userId}")
    public Response<UserResponse> update(Authentication authentication,
                                               @PathVariable  @Positive Long userId,
                                               @RequestBody @Valid UpdateUserRequest request) {
        return Response.ofSucceeded(userService.update(authentication, userId,request));
    }

    @PreAuthorize("hasPermission('user', T(vn.com.mbbank.adminportal.core.model.BitmaskValue).INSERT)")
    @PostMapping
    public CompletableFuture<Response<UserResponse>> create(Authentication authentication, @Valid @RequestBody CreateUserRequest createUserRequest) {
      return userService.create(createUserRequest).thenApply(Response::ofSucceeded);
    }


    @PreAuthorize("hasPermission('user', T(vn.com.mbbank.adminportal.core.model.BitmaskValue).VIEW)")
    @GetMapping
    public Response<PageImpl<UserResponse>> getUsers(Authentication authentication,
                                                     @Valid UserFilter request, @RequestParam int page, @RequestParam int size,
                                                     @RequestParam(required = false, defaultValue = "id:ASC") String sort) {
        return Response.ofSucceeded(userService.getUsers(authentication,request, Pageables.of(page, size, sort)));
    }

    @PreAuthorize("hasPermission('user', T(vn.com.mbbank.adminportal.core.model.BitmaskValue).VIEW)")
    @GetMapping("/{id}/detail")
    public Response<UserResponse> getUser(@PathVariable @Positive Long id) {
        return Response.ofSucceeded(userService.getUser(id));
    }

    @GetMapping("/permissions")
    public Response<List<UserPermissionsResp>> getPermission() {
        return Response.ofSucceeded(userService.getPermissions());
    }

    @GetMapping("/me")
    public Response<UserResponse> getPersonalInfo(Authentication authentication) {
        return Response.ofSucceeded(userService.getPersonalInfo(authentication));
    }
}