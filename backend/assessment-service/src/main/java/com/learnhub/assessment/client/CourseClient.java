package com.learnhub.assessment.client;

import com.learnhub.assessment.config.ServicesProperties;
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

/** Talks to course-service over HTTP. */
@Component
public class CourseClient {

    private static final Logger log = LoggerFactory.getLogger(CourseClient.class);

    private final RestClient restClient;

    @Autowired // two constructors: tell Spring which one to use
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
     * GET /api/courses/{id}, relaying the caller's token so course-service applies its own
     * visibility rules (an instructor can see their own drafts).
     *
     * @return empty if course-service says 404
     */
    public Optional<CourseSummary> findCourse(Long courseId, String bearerToken) {
        try {
            return Optional.ofNullable(restClient.get()
                    .uri("/api/courses/{id}", courseId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
                    .retrieve()
                    .body(CourseSummary.class));
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        } catch (RestClientException e) {
            log.warn("Call to course-service for course {} failed: {}", courseId, e.getMessage());
            throw new DependencyUnavailableException("Course service", e);
        }
    }
}
