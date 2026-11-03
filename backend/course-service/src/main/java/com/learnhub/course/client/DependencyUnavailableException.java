package com.learnhub.course.client;

/** Another service we depend on is down, timed out, or answered with an unexpected error. */
public class DependencyUnavailableException extends RuntimeException {

    public DependencyUnavailableException(String serviceName, Throwable cause) {
        super(serviceName + " is unavailable, please try again later", cause);
    }
}
