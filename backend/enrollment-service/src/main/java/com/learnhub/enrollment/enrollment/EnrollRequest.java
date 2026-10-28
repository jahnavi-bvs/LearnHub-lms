package com.learnhub.enrollment.enrollment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** JSON body of POST /api/enrollments. */
public record EnrollRequest(

        @NotNull(message = "Course id is required")
        @Positive(message = "Course id must be a positive number")
        Long courseId
) {
}
