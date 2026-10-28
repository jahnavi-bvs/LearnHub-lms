package com.learnhub.assessment.submission;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    // "AssessmentId" walks the relationship: WHERE assessment_id = ?
    boolean existsByAssessmentIdAndStudentId(Long assessmentId, Long studentId);

    // Detailed result: needs the answers list
    @EntityGraph(attributePaths = {"answers", "assessment"})
    Optional<Submission> findByAssessmentIdAndStudentId(Long assessmentId, Long studentId);

    // Lists only need the assessment's title/courseId, not answers
    @EntityGraph(attributePaths = "assessment")
    List<Submission> findByAssessmentIdOrderBySubmittedAtAsc(Long assessmentId);

    @EntityGraph(attributePaths = "assessment")
    List<Submission> findByStudentIdOrderBySubmittedAtDesc(Long studentId);
}
