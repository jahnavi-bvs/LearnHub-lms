package com.learnhub.course.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** The part of enrollment-service's EnrollmentResponse this service needs. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record EnrollmentSummary(Long id, Long courseId) {
}
