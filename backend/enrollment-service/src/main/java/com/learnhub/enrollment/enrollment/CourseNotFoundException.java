package com.learnhub.enrollment.enrollment;

/** The course doesn't exist, or isn't visible to the caller (e.g. an unpublished draft). */
public class CourseNotFoundException extends RuntimeException {

    public CourseNotFoundException(Long courseId) {
        super("Course " + courseId + " not found or not open for enrollment");
    }
}
