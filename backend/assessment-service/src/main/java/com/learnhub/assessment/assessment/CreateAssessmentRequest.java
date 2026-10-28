package com.learnhub.assessment.assessment;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

/** JSON body of POST /api/assessments. */
public record CreateAssessmentRequest(

        @NotNull(message = "Course id is required")
        @Positive(message = "Course id must be a positive number")
        Long courseId,

        @NotBlank(message = "Title is required")
        @Size(max = 200, message = "Title must be at most 200 characters")
        String title,

        @Size(max = 2000, message = "Description must be at most 2000 characters")
        String description,

        // @Valid: also run the rules inside each QuestionRequest
        @NotEmpty(message = "An assessment needs at least one question")
        @Size(max = 50, message = "An assessment can have at most 50 questions")
        List<@Valid QuestionRequest> questions
) {
}
