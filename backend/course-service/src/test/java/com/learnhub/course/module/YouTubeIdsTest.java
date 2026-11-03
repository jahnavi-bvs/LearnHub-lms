package com.learnhub.course.module;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class YouTubeIdsTest {

    private static final String ID = "dQw4w9WgXcQ";

    @Test
    void acceptsEveryCommonLinkForm() {
        assertThat(YouTubeIds.extract(ID)).contains(ID);
        assertThat(YouTubeIds.extract("https://www.youtube.com/watch?v=" + ID)).contains(ID);
        assertThat(YouTubeIds.extract("https://www.youtube.com/watch?list=PL1&v=" + ID + "&t=30s")).contains(ID);
        assertThat(YouTubeIds.extract("https://youtu.be/" + ID + "?si=abc")).contains(ID);
        assertThat(YouTubeIds.extract("https://m.youtube.com/watch?v=" + ID)).contains(ID);
        assertThat(YouTubeIds.extract("https://www.youtube.com/embed/" + ID)).contains(ID);
        assertThat(YouTubeIds.extract("https://www.youtube.com/shorts/" + ID)).contains(ID);
        assertThat(YouTubeIds.extract("  youtube.com/watch?v=" + ID + "  ")).contains(ID);
    }

    @Test
    void rejectsAnythingElse() {
        assertThat(YouTubeIds.extract(null)).isEmpty();
        assertThat(YouTubeIds.extract("")).isEmpty();
        assertThat(YouTubeIds.extract("not a video")).isEmpty();
        assertThat(YouTubeIds.extract("https://evil.example.com/watch?v=" + ID)).isEmpty();
        assertThat(YouTubeIds.extract("https://www.youtube.com/results?search_query=java")).isEmpty();
        assertThat(YouTubeIds.extract("https://www.youtube.com/watch?v=tooshort")).isEmpty();
    }
}
