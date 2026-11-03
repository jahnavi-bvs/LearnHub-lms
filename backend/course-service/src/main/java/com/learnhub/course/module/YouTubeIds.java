package com.learnhub.course.module;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Turns whatever an instructor pastes into a bare video id. Pure logic: easy to unit-test. */
public final class YouTubeIds {

    private static final Pattern BARE_ID = Pattern.compile("^[A-Za-z0-9_-]{11}$");
    private static final Pattern URL = Pattern.compile(
            "^(?:https?://)?(?:www\\.|m\\.)?(?:youtube\\.com/(?:watch\\?(?:[^#]*&)?v=|embed/|shorts/|live/)"
                    + "|youtube-nocookie\\.com/embed/|youtu\\.be/)([A-Za-z0-9_-]{11})(?:[?&#/].*)?$");

    private YouTubeIds() {
    }

    /** @return the 11-character id, or empty if this isn't a YouTube video link or id */
    public static Optional<String> extract(String input) {
        if (input == null) {
            return Optional.empty();
        }
        String text = input.trim();
        if (BARE_ID.matcher(text).matches()) {
            return Optional.of(text);
        }
        Matcher matcher = URL.matcher(text);
        return matcher.matches() ? Optional.of(matcher.group(1)) : Optional.empty();
    }
}
