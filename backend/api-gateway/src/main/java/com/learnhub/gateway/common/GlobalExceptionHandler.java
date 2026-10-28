package com.learnhub.gateway.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;

/**
 * Errors raised by the gateway itself. Errors from the services (400, 403, 404, 409...)
 * are passed through to the client untouched - they never reach this class.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthentication(AuthenticationException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED,
                "A valid bearer token is required");
        problem.setTitle("Unauthorized");
        return problem;
    }

    /** The target service is down or timed out (connection refused, read timeout...). */
    @ExceptionHandler(ResourceAccessException.class)
    public ProblemDetail handleServiceUnreachable(ResourceAccessException ex) {
        log.warn("Downstream service unreachable: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE,
                "The service handling this request is unavailable, please try again later");
        problem.setTitle("Service unavailable");
        return problem;
    }
}
