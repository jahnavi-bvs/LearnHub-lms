package com.learnhub.assessment.submission;

import com.learnhub.assessment.assessment.Question;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/** A student's own graded submission, with feedback for every question. */
public record SubmissionResultResponse(Long id, Long assessmentId, String assessmentTitle, int score, int maxScore,
                                       double percentage, Instant submittedAt, List<AnswerResult> results) {

    /** @param questions the assessment's questions, in order (loaded with their options) */
    static SubmissionResultResponse from(Submission submission, List<Question> questions) {
        List<AnswerResult> results = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            results.add(AnswerResult.from(questions.get(i), submission.getAnswers().get(i)));
        }
        return new SubmissionResultResponse(submission.getId(), submission.getAssessment().getId(),
                submission.getAssessment().getTitle(), submission.getScore(), submission.getMaxScore(),
                submission.getPercentage(), submission.getSubmittedAt(), results);
    }
}
