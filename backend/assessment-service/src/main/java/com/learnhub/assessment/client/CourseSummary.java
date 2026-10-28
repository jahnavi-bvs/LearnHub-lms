package com.learnhub.assessment.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** The parts of course-service's CourseResponse this service needs. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CourseSummary(Long id, String title, Long instructorId, boolean published) {
}
