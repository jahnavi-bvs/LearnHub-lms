package com.learnhub.auth.auth;

import jakarta.validation.constraints.NotBlank;

/** JSON body of POST /api/auth/login. */
public record LoginRequest(

        @NotBlank(message = "Email is required")
        String email,

        @NotBlank(message = "Password is required")
        String password
) {
}
