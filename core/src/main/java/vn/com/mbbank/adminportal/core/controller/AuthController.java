package vn.com.mbbank.adminportal.core.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.com.mbbank.adminportal.common.model.response.Response;
import vn.com.mbbank.adminportal.core.model.request.CreateRoleRequest;
import vn.com.mbbank.adminportal.core.model.request.LoginRequest;
import vn.com.mbbank.adminportal.core.model.response.AuthResponse;
import vn.com.mbbank.adminportal.core.model.response.CreateRoleResponse;
import vn.com.mbbank.adminportal.core.service.UserService;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final UserService userService;
    @PostMapping
    public Response<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        return Response.ofSucceeded(userService.login(loginRequest));
    }

    @GetMapping
    @PreAuthorize("hasPermission('user', T(vn.com.mbbank.adminportal.core.model.BitmaskValue).VIEW)")
    public Response<String> test() {
        return Response.ofSucceeded("hello");
    }
}
