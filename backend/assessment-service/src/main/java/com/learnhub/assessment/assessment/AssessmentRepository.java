package com.learnhub.assessment.assessment;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssessmentRepository extends JpaRepository<Assessment, Long> {

    /**
     * @EntityGraph loads the assessment, its questions AND their options in one query,
     * so they're usable after the repository's transaction ends (no LazyInitializationException)
     * and we avoid one extra query per question (the "N+1 problem").
     * ("WithQuestions" is just a readable name - Spring Data ignores text between "find" and "By".)
     */
    @EntityGraph(attributePaths = {"questions", "questions.options"})
    Optional<Assessment> findWithQuestionsById(Long id);

    @EntityGraph(attributePaths = "questions") // needed for questionCount in the list view
    List<Assessment> findByCourseIdOrderByCreatedAtAsc(Long courseId);
}
