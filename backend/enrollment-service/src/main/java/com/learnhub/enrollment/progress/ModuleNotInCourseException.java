package com.learnhub.enrollment.progress;

public class ModuleNotInCourseException extends RuntimeException {

    public ModuleNotInCourseException(Long moduleId) {
        super("Module " + moduleId + " is not part of this course");
    }
}
