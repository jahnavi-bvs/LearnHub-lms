package com.learnhub.assessment.assessment;

public class CourseNotFoundException extends RuntimeException {

    public CourseNotFoundException(Long courseId) {
        super("Course " + courseId + " not found");
    }
}
