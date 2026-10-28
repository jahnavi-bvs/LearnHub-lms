package com.learnhub.enrollment.client;

import com.learnhub.enrollment.config.ServicesProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Optional;

/**
 * Talks to course-service over HTTP. Everything about the remote call lives here,
 * so the rest of enrollment-service just sees "Optional<CourseSummary>".
 */
@Component
public class CourseClient {

    private static final Logger log = LoggerFactory.getLogger(CourseClient.class);

    private final RestClient restClient;

    // With two constructors Spring can't guess which to use; @Autowired marks the one for Spring
    @Autowired
    public CourseClient(ServicesProperties properties) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(properties.connectTimeout());
        requestFactory.setReadTimeout(properties.readTimeout());

        this.restClient = RestClient.builder()
                .baseUrl(properties.courseUrl())
                .requestFactory(requestFactory)
                .build();
    }

    /** For tests: use a RestClient wired to a mock server. */
    CourseClient(RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * GET /api/courses/{id} on course-service.
     *
     * @param bearerToken the caller's token to relay, or null to ask anonymously
     *                    (anonymous callers only ever see PUBLISHED courses)
     * @return empty if course-service says 404 (missing, or a draft we may not see)
     * @throws CourseServiceUnavailableException if course-service is down or misbehaves
     */
    public Optional<CourseSummary> findCourse(Long courseId, String bearerToken) {
        try {
            CourseSummary course = restClient.get()
                    .uri("/api/courses/{id}", courseId)
                    .headers(headers -> {
                        if (bearerToken != null) {
                            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken);
                        }
                    })
                    .retrieve() // throws HttpClientErrorException (4xx) / HttpServerErrorException (5xx)
                    .body(CourseSummary.class);
            return Optional.ofNullable(course);
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        } catch (RestClientException e) {
            // Connection refused, timeout, 5xx, unexpected 4xx...
            log.warn("Call to course-service for course {} failed: {}", courseId, e.getMessage());
            throw new CourseServiceUnavailableException(e);
        }
    }
}
