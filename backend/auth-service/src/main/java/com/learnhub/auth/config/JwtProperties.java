package com.learnhub.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Binds the "learnhub.jwt.*" block of application.yaml to a typed object,
 * so code asks for jwtProperties.expiration() instead of parsing strings.
 */
@ConfigurationProperties(prefix = "learnhub.jwt")
public record JwtProperties(String secret, String issuer, Duration expiration) {
}
