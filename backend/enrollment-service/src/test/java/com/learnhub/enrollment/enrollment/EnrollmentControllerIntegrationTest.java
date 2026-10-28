package com.learnhub.enrollment.enrollment;

import com.jayway.jsonpath.JsonPath;
import com.learnhub.enrollment.client.CourseClient;
import com.learnhub.enrollment.client.CourseServiceUnavailableException;
import com.learnhub.enrollment.client.CourseSummary;
import com.learnhub.enrollment.config.JwtProperties;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Real database + real JWT checks; course-service is replaced by a Mockito mock
 * so these tests don't need it running.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EnrollmentControllerIntegrationTest {

    private static final CourseSummary JAVA_COURSE = new CourseSummary(6L, "Java for Beginners", 11L, "Ira", true);

    @Autowired
    MockMvc mockMvc;

    @Autowired
    SecretKey jwtSigningKey;

    @Autowired
    JwtProperties jwtProperties;

    @MockitoBean // replaces the real CourseClient bean in the application context
    CourseClient courseClient;

    private String token(long userId, String role) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(jwtProperties.issuer())
                .subject(String.valueOf(userId))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(300))
                .claim("name", "User " + userId)
                .claim("role", role)
                .build();
        return "Bearer " + new NimbusJwtEncoder(new ImmutableSecret<>(jwtSigningKey))
                .encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
                .getTokenValue();
    }

    private org.springframework.test.web.servlet.ResultActions enroll(String bearer, long courseId) throws Exception {
        return mockMvc.perform(post("/api/enrollments").header(HttpHeaders.AUTHORIZATION, bearer)
                .contentType(MediaType.APPLICATION_JSON).content("{\"courseId\": " + courseId + "}"));
    }

    @Test
    void student_enrollsInPublishedCourse_201() throws Exception {
        when(courseClient.findCourse(eq(6L), isNull())).thenReturn(Optional.of(JAVA_COURSE));

        enroll(token(3, "STUDENT"), 6)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.studentId").value(3))
                .andExpect(jsonPath("$.studentName").value("User 3"))
                .andExpect(jsonPath("$.courseTitle").value("Java for Beginners"));

        mockMvc.perform(get("/api/enrollments/mine").header(HttpHeaders.AUTHORIZATION, token(3, "STUDENT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].courseId").value(6));
    }

    @Test
    void enrollingTwice_returns409() throws Exception {
        when(courseClient.findCourse(eq(6L), isNull())).thenReturn(Optional.of(JAVA_COURSE));

        enroll(token(3, "STUDENT"), 6).andExpect(status().isCreated());
        enroll(token(3, "STUDENT"), 6)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Already enrolled"));
    }

    @Test
    void missingOrDraftCourse_returns404() throws Exception {
        when(courseClient.findCourse(eq(99L), isNull())).thenReturn(Optional.empty());

        enroll(token(3, "STUDENT"), 99)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Course not found"));
    }

    @Test
    void courseServiceDown_returns503() throws Exception {
        when(courseClient.findCourse(eq(6L), isNull()))
                .thenThrow(new CourseServiceUnavailableException(new RuntimeException("connection refused")));

        enroll(token(3, "STUDENT"), 6)
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.title").value("Dependency unavailable"));
    }

    @Test
    void instructor_cannotEnroll_403() throws Exception {
        enroll(token(11, "INSTRUCTOR"), 6).andExpect(status().isForbidden());
    }

    @Test
    void missingCourseId_returns400() throws Exception {
        mockMvc.perform(post("/api/enrollments").header(HttpHeaders.AUTHORIZATION, token(3, "STUDENT"))
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.courseId").value("Course id is required"));
    }

    @Test
    void student_canCancelOwnEnrollment_butNotSomeoneElses() throws Exception {
        when(courseClient.findCourse(eq(6L), isNull())).thenReturn(Optional.of(JAVA_COURSE));
        String body = enroll(token(3, "STUDENT"), 6).andReturn().getResponse().getContentAsString();
        long id = ((Number) JsonPath.read(body, "$.id")).longValue();

        mockMvc.perform(delete("/api/enrollments/" + id).header(HttpHeaders.AUTHORIZATION, token(4, "STUDENT")))
                .andExpect(status().isForbidden());
        mockMvc.perform(delete("/api/enrollments/" + id).header(HttpHeaders.AUTHORIZATION, token(3, "STUDENT")))
                .andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/enrollments/" + id).header(HttpHeaders.AUTHORIZATION, token(3, "STUDENT")))
                .andExpect(status().isNotFound());
    }

    @Test
    void roster_visibleToCourseInstructorOnly() throws Exception {
        when(courseClient.findCourse(eq(6L), isNull())).thenReturn(Optional.of(JAVA_COURSE));
        when(courseClient.findCourse(eq(6L), any(String.class))).thenReturn(Optional.of(JAVA_COURSE));
        enroll(token(3, "STUDENT"), 6).andExpect(status().isCreated());

        mockMvc.perform(get("/api/enrollments/course/6").header(HttpHeaders.AUTHORIZATION, token(11, "INSTRUCTOR")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].studentId").value(3));

        // A different instructor
        mockMvc.perform(get("/api/enrollments/course/6").header(HttpHeaders.AUTHORIZATION, token(12, "INSTRUCTOR")))
                .andExpect(status().isForbidden());
    }

    @Test
    void noToken_returns401() throws Exception {
        mockMvc.perform(get("/api/enrollments/mine"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title").value("Unauthorized"));
    }
}
