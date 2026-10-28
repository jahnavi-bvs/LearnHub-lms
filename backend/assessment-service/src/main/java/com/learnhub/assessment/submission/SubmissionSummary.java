package com.learnhub.assessment.submission;

import java.time.Instant;

/** One row in a list of submissions (no per-question detail). */
public record SubmissionSummary(Long id, Long assessmentId, String assessmentTitle, Long courseId, Long studentId,
                                String studentName, int score, int maxScore, double percentage,
                                Instant submittedAt) {

    static SubmissionSummary from(Submission submission) {
        return new SubmissionSummary(submission.getId(), submission.getAssessment().getId(),
                submission.getAssessment().getTitle(), submission.getAssessment().getCourseId(),
                submission.getStudentId(), submission.getStudentName(), submission.getScore(),
                submission.getMaxScore(), submission.getPercentage(), submission.getSubmittedAt());
    }
}
