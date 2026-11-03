package com.learnhub.course.module;

/** A module's full content: for enrolled students, the course's instructor and admins. */
public record ModuleResponse(Long id, int position, String title, String description, String youtubeVideoId,
                             String notes) {

    static ModuleResponse from(CourseModule module) {
        return new ModuleResponse(module.getId(), module.getPosition(), module.getTitle(), module.getDescription(),
                module.getYoutubeVideoId(), module.getNotes());
    }
}
