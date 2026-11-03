package com.learnhub.course.module;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** JSON body for creating (POST) or replacing (PUT) a module. */
public record ModuleRequest(

        @NotBlank(message = "Title is required")
        @Size(max = 200, message = "Title must be at most 200 characters")
        String title,

        @Size(max = 500, message = "Description must be at most 500 characters")
        String description,

        // Optional: any YouTube link or a bare id. Empty means "video still needed"
        String youtubeUrl,

        @Size(max = 20000, message = "Notes must be at most 20000 characters")
        String notes
) {

    @AssertTrue(message = "Enter a valid YouTube video link")
    public boolean isYoutubeUrlValid() {
        return youtubeUrl == null || youtubeUrl.isBlank() || YouTubeIds.extract(youtubeUrl).isPresent();
    }

    /** The id to store, or null when no video was given. */
    String videoId() {
        return YouTubeIds.extract(youtubeUrl).orElse(null);
    }
}
