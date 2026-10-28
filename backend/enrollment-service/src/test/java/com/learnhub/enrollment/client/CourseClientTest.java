package com.learnhub.enrollment.client;

import com.learnhub.enrollment.config.ServicesProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.headerDoesNotExist;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/** MockRestServiceServer plays the part of course-service - no real HTTP, no Spring context. */
class CourseClientTest {

    private MockRestServiceServer courseService;
    private CourseClient courseClient;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://course-service");
        courseService = MockRestServiceServer.bindTo(builder).build();
        courseClient = new CourseClient(builder.build());
    }

    @Test
    void found_returnsCourse_andIgnoresExtraFields() {
        courseService.expect(requestTo("http://course-service/api/courses/6"))
                .andExpect(headerDoesNotExist(HttpHeaders.AUTHORIZATION))
                .andRespond(withSuccess("""
                        {"id": 6, "title": "Java", "instructorId": 11, "instructorName": "Ira",
                         "published": true, "description": "extra field we don't map"}
                        """, MediaType.APPLICATION_JSON));

        CourseSummary course = courseClient.findCourse(6L, null).orElseThrow();

        assertThat(course.title()).isEqualTo("Java");
        assertThat(course.instructorId()).isEqualTo(11L);
        courseService.verify();
    }

    @Test
    void notFound_returnsEmpty() {
        courseService.expect(requestTo("http://course-service/api/courses/99"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThat(courseClient.findCourse(99L, null)).isEmpty();
    }

    @Test
    void serverError_throwsUnavailable() {
        courseService.expect(requestTo("http://course-service/api/courses/6"))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThatThrownBy(() -> courseClient.findCourse(6L, null))
                .isInstanceOf(CourseServiceUnavailableException.class);
    }

    @Test
    void relaysBearerToken_whenGiven() {
        courseService.expect(requestTo("http://course-service/api/courses/6"))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer abc.def.ghi"))
                .andRespond(withSuccess("""
                        {"id": 6, "title": "Java", "instructorId": 11, "instructorName": "Ira", "published": false}
                        """, MediaType.APPLICATION_JSON));

        assertThat(courseClient.findCourse(6L, "abc.def.ghi")).isPresent();
        courseService.verify();
    }

    @Test
    void serviceDown_throwsUnavailable() {
        // Port 1 has nothing listening: a real "connection refused"
        CourseClient client = new CourseClient(
                new ServicesProperties("http://localhost:1", Duration.ofSeconds(1), Duration.ofSeconds(1)));

        assertThatThrownBy(() -> client.findCourse(6L, null))
                .isInstanceOf(CourseServiceUnavailableException.class);
    }
}
