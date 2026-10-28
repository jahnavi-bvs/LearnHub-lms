package com.learnhub.enrollment.enrollment;

import org.springframework.security.oauth2.jwt.Jwt;

/**
 * The caller, as described by their (already verified) JWT.
 * tokenValue is kept so it can be relayed to other services.
 */
public record CurrentUser(Long id, String name, String role, String tokenValue) {

    public static CurrentUser from(Jwt jwt) {
        return new CurrentUser(Long.valueOf(jwt.getSubject()), jwt.getClaimAsString("name"),
                jwt.getClaimAsString("role"), jwt.getTokenValue());
    }

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }
}
