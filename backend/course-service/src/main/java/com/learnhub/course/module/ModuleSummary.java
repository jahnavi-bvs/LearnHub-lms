package com.learnhub.course.module;

/** One row of a course's public outline: no video and no notes. */
public record ModuleSummary(Long id, int position, String title, String description, boolean hasVideo) {

    static ModuleSummary from(CourseModule module) {
        return new ModuleSummary(module.getId(), module.getPosition(), module.getTitle(), module.getDescription(),
                module.getYoutubeVideoId() != null);
    }
}
