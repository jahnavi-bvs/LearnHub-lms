package com.learnhub.enrollment.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/** Binds "learnhub.services.*": how to reach the other microservices. */
@ConfigurationProperties(prefix = "learnhub.services")
public record ServicesProperties(String courseUrl, Duration connectTimeout, Duration readTimeout) {
}
