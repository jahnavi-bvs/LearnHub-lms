package com.learnhub.assessment.assessment;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record QuestionRequest(

        @NotBlank(message = "Question text is required")
        @Size(max = 1000, message = "Question text must be at most 1000 characters")
        String text,

        // Constraints inside <...> apply to each option in the list
        @NotNull(message = "Options are required")
        @Size(min = 2, max = 6, message = "A question needs between 2 and 6 options")
        List<@NotBlank(message = "Options cannot be blank") @Size(max = 500) String> options,

        @NotNull(message = "Correct option index is required")
        Integer correctOptionIndex
) {

    /** Cross-field rule: the answer must point at one of the options (0-based). */
    @AssertTrue(message = "Correct option index must point to one of the options (0-based)")
    public boolean isCorrectOptionInRange() {
        if (options == null || correctOptionIndex == null) {
            return true; // the @NotNull rules above report those cases
        }
        return correctOptionIndex >= 0 && correctOptionIndex < options.size();
    }
}
