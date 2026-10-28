package com.learnhub.assessment.submission;

import com.learnhub.assessment.assessment.Question;

import java.util.List;

/**
 * Pure grading logic: no database, no HTTP, no Spring. The same input always gives
 * the same output, which makes it trivial to unit-test.
 */
public final class Grader {

    private Grader() {
    }

    /** One point per question whose chosen option is the correct one. */
    public static int score(List<Question> questions, List<Integer> answers) {
        int score = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).getCorrectOptionIndex() == answers.get(i)) {
                score++;
            }
        }
        return score;
    }

    /**
     * Checks answers against the quiz: one answer per question, each pointing at a real option.
     *
     * @return a description of the first problem, or null if the answers are valid
     */
    public static String validate(List<Question> questions, List<Integer> answers) {
        if (answers.size() != questions.size()) {
            return "Expected " + questions.size() + " answers but got " + answers.size();
        }
        for (int i = 0; i < questions.size(); i++) {
            int optionCount = questions.get(i).getOptions().size();
            int answer = answers.get(i);
            if (answer < 0 || answer >= optionCount) {
                return "Answer " + (i + 1) + " must be between 0 and " + (optionCount - 1);
            }
        }
        return null;
    }
}
