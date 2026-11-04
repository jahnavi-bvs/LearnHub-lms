package com.learnhub.enrollment.enrollment;

import com.learnhub.enrollment.client.CourseClient;
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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Every test's data is rolled back. */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EnrollmentStatsIntegrationTest {

    private static final long COURSE_A = 700L;
    private static final long COURSE_B = 701L;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    SecretKey jwtSigningKey;

    @Autowired
    JwtProperties jwtProperties;

    @MockitoBean
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

    private void enroll(long student, long course) throws Exception {
        when(courseClient.findCourse(eq(course), isNull()))
                .thenReturn(Optional.of(new CourseSummary(course, "Course", 11L, "Ira", true)));
        mockMvc.perform(post("/api/enrollments").header(HttpHeaders.AUTHORIZATION, token(student, "STUDENT"))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"courseId\": " + course + "}"))
                .andExpect(status().isCreated());
    }

    @Test
    void admin_seesTheCountForEachCourse() throws Exception {
        enroll(801, COURSE_A);
        enroll(802, COURSE_A);
        enroll(801, COURSE_B);

        mockMvc.perform(get("/api/enrollments/stats/by-course").header(HttpHeaders.AUTHORIZATION, token(1, "ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.courseId == " + COURSE_A + ")].enrollments").value(2))
                .andExpect(jsonPath("$[?(@.courseId == " + COURSE_B + ")].enrollments").value(1));
    }

    @Test
    void instructorsAndStudents_cannotSeeStats() throws Exception {
        mockMvc.perform(get("/api/enrollments/stats/by-course").header(HttpHeaders.AUTHORIZATION, token(11, "INSTRUCTOR")))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/enrollments/stats/by-course").header(HttpHeaders.AUTHORIZATION, token(801, "STUDENT")))
                .andExpect(status().isForbidden());
    }

    @Test
    void noToken_returns401() throws Exception {
        mockMvc.perform(get("/api/enrollments/stats/by-course")).andExpect(status().isUnauthorized());
    }
}
