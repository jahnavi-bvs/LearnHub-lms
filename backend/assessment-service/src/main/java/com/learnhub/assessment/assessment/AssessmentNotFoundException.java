package com.learnhub.assessment.assessment;

public class AssessmentNotFoundException extends RuntimeException {

    public AssessmentNotFoundException(Long id) {
        super("Assessment " + id + " not found");
    }
}
