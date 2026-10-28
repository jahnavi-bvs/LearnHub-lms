package com.learnhub.assessment.assessment;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * correctOptionIndex is null for students, and NON_NULL leaves it out of the JSON
 * entirely - students never receive the answer key.
 */
public record QuestionResponse(Long id, String text, List<String> options,
                               @JsonInclude(JsonInclude.Include.NON_NULL) Integer correctOptionIndex) {

    static QuestionResponse from(Question question, boolean includeAnswer) {
        return new QuestionResponse(question.getId(), question.getText(), List.copyOf(question.getOptions()),
                includeAnswer ? question.getCorrectOptionIndex() : null);
    }
}
