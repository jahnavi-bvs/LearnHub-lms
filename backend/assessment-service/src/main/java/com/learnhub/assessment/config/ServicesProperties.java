package com.learnhub.assessment.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/** Binds "learnhub.services.*": how to reach the other microservices. */
@ConfigurationProperties(prefix = "learnhub.services")
public record ServicesProperties(String courseUrl, String enrollmentUrl, Duration connectTimeout,
                                 Duration readTimeout) {
}
