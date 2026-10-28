package com.learnhub.enrollment.client;

/** course-service is down, timed out, or answered with an unexpected error. */
public class CourseServiceUnavailableException extends RuntimeException {

    public CourseServiceUnavailableException(Throwable cause) {
        super("Course service is unavailable, please try again later", cause);
    }
}
