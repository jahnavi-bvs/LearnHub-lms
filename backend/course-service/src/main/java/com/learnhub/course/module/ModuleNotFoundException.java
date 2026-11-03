package com.learnhub.course.module;

public class ModuleNotFoundException extends RuntimeException {

    public ModuleNotFoundException(Long id) {
        super("Module " + id + " not found in this course");
    }
}
