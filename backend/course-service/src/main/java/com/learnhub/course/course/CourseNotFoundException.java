package com.learnhub.course.course;

public class CourseNotFoundException extends RuntimeException {

    public CourseNotFoundException(Long id) {
        super("Course " + id + " not found");
    }
}
