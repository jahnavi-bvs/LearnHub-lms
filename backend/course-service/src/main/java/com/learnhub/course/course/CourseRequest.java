package com.learnhub.course.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** JSON body for creating (POST) or replacing (PUT) a course. */
public record CourseRequest(

        @NotBlank(message = "Title is required")
        @Size(max = 200, message = "Title must be at most 200 characters")
        String title,

        @Size(max = 5000, message = "Description must be at most 5000 characters")
        String description,

        // Optional: omitted means false (a draft)
        Boolean published
) {

    public boolean isPublishedOrDefault() {
        return Boolean.TRUE.equals(published);
    }
}
