package com.learnhub.enrollment.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
public class SecurityConfig {

    private final HandlerExceptionResolver exceptionResolver;

    /** Routes filter-level 401/403 into GlobalExceptionHandler so they get JSON bodies. */
    public SecurityConfig(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver) {
        this.exceptionResolver = exceptionResolver;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/enrollments/stats/by-course").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/enrollments").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.GET, "/api/enrollments/mine", "/api/enrollments/mine/progress").hasRole("STUDENT")
                        // "is it YOUR enrollment?" is checked in ProgressService
                        .requestMatchers("/api/enrollments/{id}/completed-modules/{moduleId}").hasRole("STUDENT")
                        // "is it YOUR course?" is checked by asking course-service
                        .requestMatchers(HttpMethod.GET, "/api/enrollments/course/{courseId}").hasAnyRole("INSTRUCTOR", "ADMIN")
                        // "is it YOUR enrollment?" is checked in EnrollmentService
                        .requestMatchers(HttpMethod.DELETE, "/api/enrollments/{id}").authenticated()
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler()));
        return http.build();
    }

    /** "role": "STUDENT" in the token becomes the authority ROLE_STUDENT. */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authorities = new JwtGrantedAuthoritiesConverter();
        authorities.setAuthoritiesClaimName("role");
        authorities.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authorities);
        return converter;
    }

    private AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Bearer");
            exceptionResolver.resolveException(request, response, null, authException);
        };
    }

    private AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) ->
                exceptionResolver.resolveException(request, response, null, accessDeniedException);
    }
}
