package com.learnhub.auth.auth;

import com.learnhub.auth.user.Role;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * JSON body of POST /api/auth/register. A record is an immutable data class:
 * Java generates the constructor, accessors (fullName(), email()...), equals and toString.
 */
public record RegisterRequest(

        @NotBlank(message = "Full name is required")
        @Size(max = 100, message = "Full name must be at most 100 characters")
        String fullName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be a valid email address")
        @Size(max = 254, message = "Email must be at most 254 characters")
        String email,

        // BCrypt only uses the first 72 bytes of a password
        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters")
        String password,

        // Optional: defaults to STUDENT when omitted
        Role role
) {

    /** Nobody may make themselves an ADMIN through public registration. */
    @AssertTrue(message = "Role must be STUDENT or INSTRUCTOR")
    public boolean isRoleAllowed() {
        return role != Role.ADMIN;
    }
}
