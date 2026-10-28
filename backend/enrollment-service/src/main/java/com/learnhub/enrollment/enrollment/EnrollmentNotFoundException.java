package com.learnhub.enrollment.enrollment;

public class EnrollmentNotFoundException extends RuntimeException {

    public EnrollmentNotFoundException(Long id) {
        super("Enrollment " + id + " not found");
    }
}
