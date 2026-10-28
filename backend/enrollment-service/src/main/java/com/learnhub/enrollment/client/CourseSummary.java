package com.learnhub.enrollment.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The parts of course-service's CourseResponse that this service needs.
 * ignoreUnknown: course-service may add fields later without breaking us.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CourseSummary(Long id, String title, Long instructorId, String instructorName, boolean published) {
}
