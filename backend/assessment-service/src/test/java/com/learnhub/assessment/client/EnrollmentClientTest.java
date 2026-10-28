package com.learnhub.assessment.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/** MockRestServiceServer plays the part of enrollment-service. */
class EnrollmentClientTest {

    private static final String MY_ENROLLMENTS = """
            [{"id": 1, "studentId": 3, "courseId": 6, "courseTitle": "Java"},
             {"id": 2, "studentId": 3, "courseId": 9, "courseTitle": "Python"}]
            """;

    private MockRestServiceServer enrollmentService;
    private EnrollmentClient enrollmentClient;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://enrollment-service");
        enrollmentService = MockRestServiceServer.bindTo(builder).build();
        enrollmentClient = new EnrollmentClient(builder.build());
    }

    @Test
    void enrolled_whenCourseIsInStudentsList() {
        enrollmentService.expect(requestTo("http://enrollment-service/api/enrollments/mine"))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer student-token"))
                .andRespond(withSuccess(MY_ENROLLMENTS, MediaType.APPLICATION_JSON));

        assertThat(enrollmentClient.isEnrolled(6L, "student-token")).isTrue();
        enrollmentService.verify();
    }

    @Test
    void notEnrolled_whenCourseIsMissingFromList() {
        enrollmentService.expect(requestTo("http://enrollment-service/api/enrollments/mine"))
                .andRespond(withSuccess(MY_ENROLLMENTS, MediaType.APPLICATION_JSON));

        assertThat(enrollmentClient.isEnrolled(7L, "student-token")).isFalse();
    }

    @Test
    void serverError_throwsUnavailable() {
        enrollmentService.expect(requestTo("http://enrollment-service/api/enrollments/mine"))
                .andRespond(withStatus(HttpStatus.BAD_GATEWAY));

        assertThatThrownBy(() -> enrollmentClient.isEnrolled(6L, "student-token"))
                .isInstanceOf(DependencyUnavailableException.class);
    }
}
