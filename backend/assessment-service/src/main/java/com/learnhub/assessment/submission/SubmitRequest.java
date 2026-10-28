package com.learnhub.assessment.submission;

import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * JSON body of POST /api/assessments/{id}/submissions: the chosen option index (0-based)
 * for each question, in order. Whether the count and ranges fit the quiz is checked by
 * Grader.validate, because that depends on data stored in the database.
 */
public record SubmitRequest(

        @NotNull(message = "Answers are required")
        List<@NotNull(message = "Every question needs an answer") Integer> answers
) {
}
