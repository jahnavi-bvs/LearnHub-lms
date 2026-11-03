package com.learnhub.course.client;

import com.learnhub.course.config.ServicesProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Arrays;

/** Talks to enrollment-service over HTTP. */
@Component
public class EnrollmentClient {

    private static final Logger log = LoggerFactory.getLogger(EnrollmentClient.class);

    private final RestClient restClient;

    public EnrollmentClient(ServicesProperties properties) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(properties.connectTimeout());
        requestFactory.setReadTimeout(properties.readTimeout());
        this.restClient = RestClient.builder()
                .baseUrl(properties.enrollmentUrl())
                .requestFactory(requestFactory)
                .build();
    }

    /** Is this course among the STUDENT's own enrollments? Their token is relayed. */
    public boolean isEnrolled(Long courseId, String studentToken) {
        try {
            EnrollmentSummary[] enrollments = restClient.get()
                    .uri("/api/enrollments/mine")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + studentToken)
                    .retrieve()
                    .body(EnrollmentSummary[].class);
            return enrollments != null
                    && Arrays.stream(enrollments).anyMatch(e -> courseId.equals(e.courseId()));
        } catch (RestClientException e) {
            log.warn("Call to enrollment-service failed: {}", e.getMessage());
            throw new DependencyUnavailableException("Enrollment service", e);
        }
    }
}
