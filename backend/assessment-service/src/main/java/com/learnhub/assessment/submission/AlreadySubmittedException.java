package com.learnhub.assessment.submission;

public class AlreadySubmittedException extends RuntimeException {

    public AlreadySubmittedException(Long assessmentId) {
        super("You have already submitted assessment " + assessmentId);
    }
}
