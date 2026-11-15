package com.learnhub.gateway.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.time.Duration;
import java.util.List;

/**
 * Edge security: which URLs are public, and "is this token genuine?" for everything else.
 * Fine-grained rules (roles, ownership) stay in each service, which re-checks the token too.
 */
@Configuration
public class SecurityConfig {

    private final HandlerExceptionResolver exceptionResolver;

    public SecurityConfig(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver) {
        this.exceptionResolver = exceptionResolver;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Uses the CorsConfigurationSource bean below; answers browser preflight (OPTIONS) requests
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login").permitAll()
                        // Public catalog. (/api/courses/mine also matches {id}; course-service protects it itself.)
                        .requestMatchers(HttpMethod.GET, "/api/courses", "/api/courses/{id}", "/api/courses/{id}/modules").permitAll()
                        // Enrollment totals for "Popular now" on the public landing page
                        .requestMatchers(HttpMethod.GET, "/api/enrollments/stats/by-course").permitAll()
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated())
                // No custom role converter here: the gateway never checks roles
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                        .authenticationEntryPoint(authenticationEntryPoint()));
        return http.build();
    }

    /**
     * CORS: the browser only lets React (a different origin) call this API if we say so.
     * Only the gateway needs this - the browser never talks to the services directly.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${learnhub.cors.allowed-origins}") List<String> allowedOrigins) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of(HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_TYPE));
        // Let JavaScript read the Location header of 201 responses
        config.setExposedHeaders(List.of(HttpHeaders.LOCATION));
        config.setMaxAge(Duration.ofHours(1)); // browser may cache the preflight answer

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }

    /** 401 with a JSON body, same shape as the services' errors. */
    private AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Bearer");
            exceptionResolver.resolveException(request, response, null, authException);
        };
    }
}
