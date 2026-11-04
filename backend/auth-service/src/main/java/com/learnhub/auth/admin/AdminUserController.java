package com.learnhub.auth.admin;

import com.learnhub.auth.auth.UserResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Every endpoint here is ADMIN only: see SecurityConfig. */
@RestController
@RequestMapping("/api/auth/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    public List<UserResponse> list() {
        return adminUserService.listAll();
    }

    @PutMapping("/{id}/active")
    public UserResponse setActive(@PathVariable Long id, @Valid @RequestBody UpdateActiveRequest request,
                                  @AuthenticationPrincipal Jwt jwt) {
        return adminUserService.setActive(id, request.active(), Long.valueOf(jwt.getSubject()));
    }

    @PutMapping("/{id}/role")
    public UserResponse setRole(@PathVariable Long id, @Valid @RequestBody UpdateRoleRequest request,
                                @AuthenticationPrincipal Jwt jwt) {
        return adminUserService.setRole(id, request.role(), Long.valueOf(jwt.getSubject()));
    }
}
