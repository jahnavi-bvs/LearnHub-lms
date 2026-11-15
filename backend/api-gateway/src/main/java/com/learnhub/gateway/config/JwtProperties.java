package com.learnhub.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Binds "learnhub.jwt.*" from application.yaml. */

@ConfigurationProperties(prefix = "learnhub.jwt")
public record JwtProperties(String secret, String issuer) {
}


