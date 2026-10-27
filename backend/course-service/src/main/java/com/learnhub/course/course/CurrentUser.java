package com.learnhub.course.course;

import org.springframework.security.oauth2.jwt.Jwt;

/**
 * The caller, as described by their (already verified) JWT.
 * Keeps the service layer independent of Spring Security classes.
 */
public record CurrentUser(Long id, String name, String role) {

    public static CurrentUser from(Jwt jwt) {
        return new CurrentUser(Long.valueOf(jwt.getSubject()), jwt.getClaimAsString("name"), jwt.getClaimAsString("role"));
    }

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }
}
