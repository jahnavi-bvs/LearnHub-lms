package com.learnhub.assessment.submission;

import com.jayway.jsonpath.JsonPath;
import com.learnhub.assessment.assessment.AssessmentRepository;
import com.learnhub.assessment.client.CourseClient;
import com.learnhub.assessment.client.CourseSummary;
import com.learnhub.assessment.client.EnrollmentClient;
import com.learnhub.assessment.config.JwtProperties;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Not @Transactional (see AssessmentControllerIntegrationTest); cleans up its own rows. */
@SpringBootTest
@AutoConfigureMockMvc
class SubmissionIntegrationTest {

    private static final long COURSE_ID = 6L;
    private static final long OWNER_ID = 11L;
    private static final long STUDENT_ID = 3L;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    SecretKey jwtSigningKey;

    @Autowired
    JwtProperties jwtProperties;

    @Autowired
    AssessmentRepository assessmentRepository;

    @Autowired
    SubmissionRepository submissionRepository;

    @MockitoBean
    CourseClient courseClient;

    @MockitoBean
    EnrollmentClient enrollmentClient;

    private long assessmentId;

    @BeforeEach
    void createQuiz() throws Exception {
        when(courseClient.findCourse(eq(COURSE_ID), anyString()))
                .thenReturn(Optional.of(new CourseSummary(COURSE_ID, "Java", OWNER_ID, true)));
        when(enrollmentClient.isEnrolled(eq(COURSE_ID), anyString())).thenReturn(true);

        String body = mockMvc.perform(post("/api/assessments")
                        .header(HttpHeaders.AUTHORIZATION, token(OWNER_ID, "INSTRUCTOR"))
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {"courseId": 6, "title": "Grading Quiz", "questions": [
                                  {"text": "Q1", "options": ["a", "b", "c"], "correctOptionIndex": 0},
                                  {"text": "Q2", "options": ["yes", "no"], "correctOptionIndex": 1},
                                  {"text": "Q3", "options": ["w", "x", "y"], "correctOptionIndex": 2}
                                ]}
                                """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        assessmentId = ((Number) JsonPath.read(body, "$.id")).longValue();
    }

    @AfterEach
    void cleanUp() {
        // Children first: submissions reference the assessment through a foreign key
        submissionRepository.deleteAll(submissionRepository.findByAssessmentIdOrderBySubmittedAtAsc(assessmentId));
        assessmentRepository.deleteById(assessmentId);
    }

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

    private ResultActions submit(long studentId, String answersJson) throws Exception {
        return mockMvc.perform(post("/api/assessments/" + assessmentId + "/submissions")
                .header(HttpHeaders.AUTHORIZATION, token(studentId, "STUDENT"))
                .contentType(MediaType.APPLICATION_JSON).content("{\"answers\": " + answersJson + "}"));
    }

    @Test
    void submit_isGraded_withPerQuestionFeedback() throws Exception {
        submit(STUDENT_ID, "[0, 0, 2]")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.score").value(2))
                .andExpect(jsonPath("$.maxScore").value(3))
                .andExpect(jsonPath("$.percentage").value(66.7))
                .andExpect(jsonPath("$.results[0].correct").value(true))
                .andExpect(jsonPath("$.results[1].correct").value(false))
                .andExpect(jsonPath("$.results[1].selectedOptionIndex").value(0))
                .andExpect(jsonPath("$.results[1].correctOptionIndex").value(1));
    }

    @Test
    void secondSubmission_returns409() throws Exception {
        submit(STUDENT_ID, "[0, 1, 2]").andExpect(status().isCreated());
        submit(STUDENT_ID, "[0, 1, 2]")
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Already submitted"));
    }

    @Test
    void wrongNumberOfAnswers_returns400() throws Exception {
        submit(STUDENT_ID, "[0, 1]")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Expected 3 answers but got 2"));
    }

    @Test
    void notEnrolled_returns403() throws Exception {
        when(enrollmentClient.isEnrolled(eq(COURSE_ID), anyString())).thenReturn(false);

        submit(STUDENT_ID, "[0, 1, 2]").andExpect(status().isForbidden());
    }

    @Test
    void myResult_afterSubmitting_includesAnswers() throws Exception {
        submit(STUDENT_ID, "[0, 1, 0]").andExpect(status().isCreated());

        mockMvc.perform(get("/api/assessments/" + assessmentId + "/submissions/mine")
                        .header(HttpHeaders.AUTHORIZATION, token(STUDENT_ID, "STUDENT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(2))
                .andExpect(jsonPath("$.results.length()").value(3))
                .andExpect(jsonPath("$.results[2].options[2]").value("y"))
                .andExpect(jsonPath("$.results[2].correct").value(false));
    }

    @Test
    void myResult_beforeSubmitting_returns404() throws Exception {
        mockMvc.perform(get("/api/assessments/" + assessmentId + "/submissions/mine")
                        .header(HttpHeaders.AUTHORIZATION, token(STUDENT_ID, "STUDENT")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Submission not found"));
    }

    @Test
    void myResults_listsAcrossAssessments() throws Exception {
        submit(STUDENT_ID, "[0, 1, 2]").andExpect(status().isCreated());

        mockMvc.perform(get("/api/assessments/submissions/mine")
                        .header(HttpHeaders.AUTHORIZATION, token(STUDENT_ID, "STUDENT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.assessmentId == " + assessmentId + ")].percentage").value(100.0));
    }

    @Test
    void owner_seesAllSubmissions_otherInstructorAndStudentCannot() throws Exception {
        submit(STUDENT_ID, "[0, 1, 2]").andExpect(status().isCreated());
        submit(4L, "[1, 1, 1]").andExpect(status().isCreated());

        mockMvc.perform(get("/api/assessments/" + assessmentId + "/submissions")
                        .header(HttpHeaders.AUTHORIZATION, token(OWNER_ID, "INSTRUCTOR")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].studentName").value("User 3"))
                .andExpect(jsonPath("$[1].score").value(1));

        mockMvc.perform(get("/api/assessments/" + assessmentId + "/submissions")
                        .header(HttpHeaders.AUTHORIZATION, token(12, "INSTRUCTOR")))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/assessments/" + assessmentId + "/submissions")
                        .header(HttpHeaders.AUTHORIZATION, token(STUDENT_ID, "STUDENT")))
                .andExpect(status().isForbidden());
    }
}
