package com.learnhub.enrollment.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** The part of course-service's module outline this service needs. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ModuleSummary(Long id) {
}
