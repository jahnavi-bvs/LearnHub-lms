package com.learnhub.enrollment.progress;

import com.jayway.jsonpath.JsonPath;
import com.learnhub.enrollment.client.CourseClient;
import com.learnhub.enrollment.client.CourseServiceUnavailableException;
import com.learnhub.enrollment.client.CourseSummary;
import com.learnhub.enrollment.config.JwtProperties;
import com.learnhub.enrollment.enrollment.EnrollmentRepository;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.web.servlet.ResultActions;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Not @Transactional: deletes must really commit, as in production. Cleans up its own rows. */
@SpringBootTest
@AutoConfigureMockMvc
class ProgressIntegrationTest {

    private static final long COURSE = 500L;
    private static final long STUDENT = 901L;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    SecretKey jwtSigningKey;

    @Autowired
    JwtProperties jwtProperties;

    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Autowired
    ModuleCompletionRepository completionRepository;

    @MockitoBean
    CourseClient courseClient;

    private long enrollmentId;

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

    @BeforeEach
    void enroll() throws Exception {
        when(courseClient.findCourse(eq(COURSE), isNull()))
                .thenReturn(Optional.of(new CourseSummary(COURSE, "Course", 11L, "Ira", true)));
        when(courseClient.findModuleIds(eq(COURSE), anyString())).thenReturn(List.of(1L, 2L, 3L, 4L));

        String body = mockMvc.perform(post("/api/enrollments").header(HttpHeaders.AUTHORIZATION, token(STUDENT, "STUDENT"))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"courseId\": " + COURSE + "}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        enrollmentId = ((Number) JsonPath.read(body, "$.id")).longValue();
    }

    @AfterEach
    void cleanUp() {
        completionRepository.deleteAll(completionRepository.findByEnrollmentId(enrollmentId));
        enrollmentRepository.findById(enrollmentId).ifPresent(enrollmentRepository::delete);
    }

    private ResultActions mark(long moduleId) throws Exception {
        return mockMvc.perform(put("/api/enrollments/" + enrollmentId + "/completed-modules/" + moduleId)
                .header(HttpHeaders.AUTHORIZATION, token(STUDENT, "STUDENT")));
    }

    @Test
    void startsAtZero_withFirstModuleNext() throws Exception {
        mockMvc.perform(get("/api/enrollments/mine/progress").header(HttpHeaders.AUTHORIZATION, token(STUDENT, "STUDENT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].percentage").value(0))
                .andExpect(jsonPath("$[0].totalModules").value(4))
                .andExpect(jsonPath("$[0].nextModuleId").value(1));
    }

    @Test
    void marking_updatesPercentage_andNextModule() throws Exception {
        mark(1).andExpect(status().isOk())
                .andExpect(jsonPath("$.percentage").value(25))
                .andExpect(jsonPath("$.completedCount").value(1))
                .andExpect(jsonPath("$.nextModuleId").value(2));
    }

    @Test
    void markingTwice_countsOnce() throws Exception {
        mark(2).andExpect(status().isOk());
        mark(2).andExpect(status().isOk()).andExpect(jsonPath("$.completedCount").value(1));
    }

    @Test
    void skippingAModule_nextIsTheFirstUnfinished() throws Exception {
        mark(2).andExpect(jsonPath("$.nextModuleId").value(1));
    }

    @Test
    void allDone_is100_withNoNextModule() throws Exception {
        mark(1);
        mark(2);
        mark(3);
        mark(4).andExpect(jsonPath("$.percentage").value(100))
                .andExpect(jsonPath("$.nextModuleId").doesNotExist());
    }

    @Test
    void unmarking_lowersProgress() throws Exception {
        mark(1);
        mark(2);
        mockMvc.perform(delete("/api/enrollments/" + enrollmentId + "/completed-modules/1")
                        .header(HttpHeaders.AUTHORIZATION, token(STUDENT, "STUDENT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.percentage").value(25))
                .andExpect(jsonPath("$.nextModuleId").value(1));
    }

    @Test
    void moduleFromAnotherCourse_returns404() throws Exception {
        mark(999).andExpect(status().isNotFound()).andExpect(jsonPath("$.title").value("Module not found"));
    }

    @Test
    void anotherStudent_cannotChangeThisProgress() throws Exception {
        mockMvc.perform(put("/api/enrollments/" + enrollmentId + "/completed-modules/1")
                        .header(HttpHeaders.AUTHORIZATION, token(902, "STUDENT")))
                .andExpect(status().isForbidden());
    }

    @Test
    void instructor_cannotMarkModules() throws Exception {
        mockMvc.perform(put("/api/enrollments/" + enrollmentId + "/completed-modules/1")
                        .header(HttpHeaders.AUTHORIZATION, token(11, "INSTRUCTOR")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deletedModule_noLongerCounts() throws Exception {
        mark(1);
        mark(4);
        when(courseClient.findModuleIds(eq(COURSE), anyString())).thenReturn(List.of(1L, 2L, 3L)); // module 4 removed

        mockMvc.perform(get("/api/enrollments/mine/progress").header(HttpHeaders.AUTHORIZATION, token(STUDENT, "STUDENT")))
                .andExpect(jsonPath("$[0].completedCount").value(1))
                .andExpect(jsonPath("$[0].percentage").value(33));
    }

    @Test
    void droppingACourse_removesItsProgress() throws Exception {
        mark(1);

        mockMvc.perform(delete("/api/enrollments/" + enrollmentId).header(HttpHeaders.AUTHORIZATION, token(STUDENT, "STUDENT")))
                .andExpect(status().isNoContent());

        assertThat(completionRepository.findByEnrollmentId(enrollmentId)).isEmpty();
    }

    @Test
    void courseServiceDown_returns503() throws Exception {
        when(courseClient.findModuleIds(eq(COURSE), anyString()))
                .thenThrow(new CourseServiceUnavailableException(new RuntimeException()));

        mark(1).andExpect(status().isServiceUnavailable());
    }
}
