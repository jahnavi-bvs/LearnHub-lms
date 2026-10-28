package com.learnhub.assessment.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** The parts of enrollment-service's EnrollmentResponse this service needs. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record EnrollmentSummary(Long id, Long courseId) {
}
