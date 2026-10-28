package com.learnhub.enrollment.common;

import com.learnhub.enrollment.client.CourseServiceUnavailableException;
import com.learnhub.enrollment.enrollment.AlreadyEnrolledException;
import com.learnhub.enrollment.enrollment.CourseNotFoundException;
import com.learnhub.enrollment.enrollment.EnrollmentNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;

/** Same RFC 9457 problem-details format as the other services. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.putIfAbsent(error.getField(), error.getDefaultMessage()));

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "One or more fields are invalid");
        problem.setTitle("Validation failed");
        problem.setProperty("errors", errors);
        return problem;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleUnreadable(HttpMessageNotReadableException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Request body is missing or is not valid JSON, or a field has the wrong type");
        problem.setTitle("Malformed request");
        return problem;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Invalid value '" + ex.getValue() + "' for parameter '" + ex.getName() + "'");
        problem.setTitle("Invalid parameter");
        return problem;
    }

    @ExceptionHandler(CourseNotFoundException.class)
    public ProblemDetail handleCourseNotFound(CourseNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Course not found");
        return problem;
    }

    @ExceptionHandler(EnrollmentNotFoundException.class)
    public ProblemDetail handleEnrollmentNotFound(EnrollmentNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Enrollment not found");
        return problem;
    }

    @ExceptionHandler(AlreadyEnrolledException.class)
    public ProblemDetail handleAlreadyEnrolled(AlreadyEnrolledException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Already enrolled");
        return problem;
    }

    /** 503: a service we depend on is down - the client may retry later. */
    @ExceptionHandler(CourseServiceUnavailableException.class)
    public ProblemDetail handleCourseServiceDown(CourseServiceUnavailableException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
        problem.setTitle("Dependency unavailable");
        return problem;
    }

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthentication(AuthenticationException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED,
                "A valid bearer token is required");
        problem.setTitle("Unauthorized");
        return problem;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN,
                "You do not have permission to perform this action");
        problem.setTitle("Forbidden");
        return problem;
    }

    @ExceptionHandler(ErrorResponseException.class)
    public ProblemDetail handleErrorResponse(ErrorResponseException ex) {
        return ex.getBody();
    }
}
