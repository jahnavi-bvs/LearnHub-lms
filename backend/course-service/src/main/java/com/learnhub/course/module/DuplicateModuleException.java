package com.learnhub.course.module;

public class DuplicateModuleException extends RuntimeException {

    public DuplicateModuleException(String title) {
        super("This course already has a module called '" + title + "'");
    }
}
