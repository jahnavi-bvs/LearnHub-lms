package com.learnhub.assessment.submission;

/** The answers don't fit the quiz (wrong count, or an option that doesn't exist). */
public class InvalidSubmissionException extends RuntimeException {

    public InvalidSubmissionException(String message) {
        super(message);
    }
}
