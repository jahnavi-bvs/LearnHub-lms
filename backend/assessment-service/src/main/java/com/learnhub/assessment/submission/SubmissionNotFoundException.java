package com.learnhub.assessment.submission;

public class SubmissionNotFoundException extends RuntimeException {

    public SubmissionNotFoundException(Long assessmentId) {
        super("You have not submitted assessment " + assessmentId + " yet");
    }
}
