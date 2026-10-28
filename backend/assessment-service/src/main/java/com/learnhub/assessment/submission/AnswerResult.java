package com.learnhub.assessment.submission;

import com.learnhub.assessment.assessment.Question;

import java.util.List;

/** Per-question feedback, shown to the student after they submit. */
public record AnswerResult(Long questionId, String questionText, List<String> options, int selectedOptionIndex,
                           int correctOptionIndex, boolean correct) {

    static AnswerResult from(Question question, int selected) {
        return new AnswerResult(question.getId(), question.getText(), List.copyOf(question.getOptions()),
                selected, question.getCorrectOptionIndex(), selected == question.getCorrectOptionIndex());
    }
}
