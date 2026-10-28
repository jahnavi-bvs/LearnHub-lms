package com.learnhub.assessment.submission;

import com.learnhub.assessment.assessment.Question;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** Plain JUnit: Grader is pure logic, so no Spring, mocks or database are needed. */
class GraderTest {

    private final List<Question> questions = List.of(
            new Question("Q1", List.of("a", "b", "c"), 0),
            new Question("Q2", List.of("yes", "no"), 1),
            new Question("Q3", List.of("w", "x", "y", "z"), 2));

    @Test
    void allCorrect() {
        assertThat(Grader.score(questions, List.of(0, 1, 2))).isEqualTo(3);
    }

    @Test
    void someCorrect() {
        assertThat(Grader.score(questions, List.of(0, 0, 2))).isEqualTo(2);
    }

    @Test
    void noneCorrect() {
        assertThat(Grader.score(questions, List.of(1, 0, 3))).isZero();
    }

    @Test
    void validate_acceptsWellFormedAnswers() {
        assertThat(Grader.validate(questions, List.of(2, 1, 3))).isNull();
    }

    @Test
    void validate_rejectsWrongNumberOfAnswers() {
        assertThat(Grader.validate(questions, List.of(0, 1))).isEqualTo("Expected 3 answers but got 2");
    }

    @Test
    void validate_rejectsOptionOutOfRange() {
        assertThat(Grader.validate(questions, List.of(0, 2, 0))).isEqualTo("Answer 2 must be between 0 and 1");
        assertThat(Grader.validate(questions, List.of(-1, 0, 0))).isEqualTo("Answer 1 must be between 0 and 2");
    }
}
