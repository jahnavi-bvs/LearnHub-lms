package com.learnhub.course.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * Verifies tokens issued by auth-service. There is deliberately no JwtEncoder here:
 * only auth-service can create tokens; this service can only check them.
 */
@Configuration
@EnableConfigurationProperties({JwtProperties.class, ServicesProperties.class})
public class JwtConfig {

    private final JwtProperties jwtProperties;

    public JwtConfig(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Bean
    public SecretKey jwtSigningKey() {
        String secret = jwtProperties.secret();
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT_SECRET is not set - add it to local-secrets.properties");
        }
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        if (keyBytes.length < 32) { // HS256 needs a key of at least 256 bits
            throw new IllegalStateException("JWT_SECRET must decode to at least 32 bytes, got " + keyBytes.length);
        }
        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    @Bean
    public JwtDecoder jwtDecoder(SecretKey jwtSigningKey) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(jwtSigningKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
        // Default checks (signature, exp/nbf with 60s clock skew) plus "iss must be learnhub-auth"
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(jwtProperties.issuer()));
        return decoder;
    }
}
