package com.learnhub.enrollment.enrollment;

public class AlreadyEnrolledException extends RuntimeException {

    public AlreadyEnrolledException(Long courseId) {
        super("You are already enrolled in course " + courseId);
    }
}
