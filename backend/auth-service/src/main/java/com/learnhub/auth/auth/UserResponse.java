package com.learnhub.auth.auth;

import com.learnhub.auth.user.Role;
import com.learnhub.auth.user.User;

import java.time.Instant;

/** What the API returns about a user. Deliberately has no password field. */
public record UserResponse(Long id, String fullName, String email, Role role, Instant createdAt) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getFullName(), user.getEmail(), user.getRole(), user.getCreatedAt());
    }
}
