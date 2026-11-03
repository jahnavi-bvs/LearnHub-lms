package com.learnhub.course.module;

public class InvalidModuleOrderException extends RuntimeException {

    public InvalidModuleOrderException() {
        super("The new order must list every module in this course exactly once");
    }
}
