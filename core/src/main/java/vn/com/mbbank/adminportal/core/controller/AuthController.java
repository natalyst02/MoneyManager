package vn.com.mbbank.adminportal.core.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}
