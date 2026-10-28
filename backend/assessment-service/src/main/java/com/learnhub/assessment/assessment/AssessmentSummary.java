package com.learnhub.assessment.assessment;

import java.time.Instant;

/** One row in a course's list of assessments (no questions). */
public record AssessmentSummary(Long id, Long courseId, String title, int questionCount, Instant createdAt) {

    static AssessmentSummary from(Assessment assessment) {
        return new AssessmentSummary(assessment.getId(), assessment.getCourseId(), assessment.getTitle(),
                assessment.getQuestions().size(), assessment.getCreatedAt());
    }
}
