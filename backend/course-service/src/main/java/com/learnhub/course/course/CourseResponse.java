package com.learnhub.course.course;

import java.time.Instant;

public record CourseResponse(Long id, String title, String description, Long instructorId, String instructorName,
                             boolean published, Instant createdAt, Instant updatedAt) {

    public static CourseResponse from(Course course) {
        return new CourseResponse(course.getId(), course.getTitle(), course.getDescription(),
                course.getInstructorId(), course.getInstructorName(), course.isPublished(),
                course.getCreatedAt(), course.getUpdatedAt());
    }
}
