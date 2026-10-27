package com.learnhub.auth.auth;

/**
 * Returned after a successful login. The client sends accessToken back on later
 * requests as the header "Authorization: Bearer <accessToken>".
 */
public record LoginResponse(String accessToken, String tokenType, long expiresIn, UserResponse user) {
}
