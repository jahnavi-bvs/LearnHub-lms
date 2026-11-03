package com.learnhub.course.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/** Binds "learnhub.services.*": how to reach the other microservices. */
@ConfigurationProperties(prefix = "learnhub.services")
public record ServicesProperties(String enrollmentUrl, Duration connectTimeout, Duration readTimeout) {
}
