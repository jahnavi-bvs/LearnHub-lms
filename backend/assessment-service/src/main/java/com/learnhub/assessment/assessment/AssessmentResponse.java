package com.learnhub.assessment.assessment;

import java.time.Instant;
import java.util.List;

/** A full assessment with its questions. */
public record AssessmentResponse(Long id, Long courseId, String title, String description, Instant createdAt,
                                 List<QuestionResponse> questions) {

    /** @param includeAnswers true only for the course's instructor or an admin */
    static AssessmentResponse from(Assessment assessment, boolean includeAnswers) {
        return new AssessmentResponse(assessment.getId(), assessment.getCourseId(), assessment.getTitle(),
                assessment.getDescription(), assessment.getCreatedAt(),
                assessment.getQuestions().stream().map(q -> QuestionResponse.from(q, includeAnswers)).toList());
    }
}
