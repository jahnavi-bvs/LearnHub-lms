package com.learnhub.assessment.assessment;

import com.jayway.jsonpath.JsonPath;
import com.learnhub.assessment.client.CourseClient;
import com.learnhub.assessment.client.CourseSummary;
import com.learnhub.assessment.client.DependencyUnavailableException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * NOT @Transactional on purpose: data is really committed, so reads go through the real
 * @EntityGraph queries with no open transaction - exactly like production. A lazy-loading
 * mistake would fail here. @AfterEach deletes only the rows these tests created.
 */
@SpringBootTest
@AutoConfigureMockMvc
class AssessmentControllerIntegrationTest {

    private static final long COURSE_ID = 6L;
    private static final long OWNER_ID = 11L;
    private static final String QUIZ_JSON = """
            {"courseId": 6, "title": "Java Basics Quiz", "description": "Chapter 1",
             "questions": [
               {"text": "Which keyword creates an object?", "options": ["new", "make", "create"], "correctOptionIndex": 0},
               {"text": "Is Java statically typed?", "options": ["Yes", "No"], "correctOptionIndex": 0}
             ]}
            """;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    SecretKey jwtSigningKey;

    @Autowired
    JwtProperties jwtProperties;

    @Autowired
    AssessmentRepository assessmentRepository;

    @MockitoBean
    CourseClient courseClient;

    @MockitoBean
    EnrollmentClient enrollmentClient;

    private final List<Long> createdIds = new ArrayList<>();

    @BeforeEach
    void courseServiceKnowsCourse6() {
        when(courseClient.findCourse(eq(COURSE_ID), anyString()))
                .thenReturn(Optional.of(new CourseSummary(COURSE_ID, "Java for Beginners", OWNER_ID, true)));
    }

    @AfterEach
    void deleteCreatedAssessments() {
        assessmentRepository.deleteAllById(createdIds); // cascade removes questions and options
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

    private ResultActions createQuiz(String bearer, String json) throws Exception {
        ResultActions result = mockMvc.perform(post("/api/assessments").header(HttpHeaders.AUTHORIZATION, bearer)
                .contentType(MediaType.APPLICATION_JSON).content(json));
        String body = result.andReturn().getResponse().getContentAsString();
        if (result.andReturn().getResponse().getStatus() == 201) {
            createdIds.add(((Number) JsonPath.read(body, "$.id")).longValue());
        }
        return result;
    }

    @Test
    void owner_createsQuiz_201_withAnswerKey() throws Exception {
        createQuiz(token(OWNER_ID, "INSTRUCTOR"), QUIZ_JSON)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.courseId").value(6))
                .andExpect(jsonPath("$.questions.length()").value(2))
                .andExpect(jsonPath("$.questions[0].options[0]").value("new"))
                .andExpect(jsonPath("$.questions[0].correctOptionIndex").value(0));
    }

    @Test
    void otherInstructor_cannotCreateQuizForCourse_403() throws Exception {
        createQuiz(token(12, "INSTRUCTOR"), QUIZ_JSON)
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.detail").value("You can only create assessments for your own courses"));
    }

    @Test
    void student_cannotCreateQuiz_403() throws Exception {
        createQuiz(token(3, "STUDENT"), QUIZ_JSON).andExpect(status().isForbidden());
    }

    @Test
    void invalidQuestions_return400_withNestedFieldErrors() throws Exception {
        createQuiz(token(OWNER_ID, "INSTRUCTOR"), """
                {"courseId": 6, "title": "Bad quiz",
                 "questions": [{"text": "Only one option?", "options": ["A"], "correctOptionIndex": 3}]}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors['questions[0].options']").exists())
                .andExpect(jsonPath("$.errors['questions[0].correctOptionInRange']").exists());
    }

    @Test
    void enrolledStudent_seesQuestions_butNotAnswers() throws Exception {
        String body = createQuiz(token(OWNER_ID, "INSTRUCTOR"), QUIZ_JSON)
                .andReturn().getResponse().getContentAsString();
        long id = ((Number) JsonPath.read(body, "$.id")).longValue();
        when(enrollmentClient.isEnrolled(eq(COURSE_ID), anyString())).thenReturn(true);

        mockMvc.perform(get("/api/assessments/" + id).header(HttpHeaders.AUTHORIZATION, token(3, "STUDENT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.questions.length()").value(2))
                .andExpect(jsonPath("$.questions[1].options[1]").value("No"))
                .andExpect(jsonPath("$.questions[0].correctOptionIndex").doesNotExist());
    }

    @Test
    void notEnrolledStudent_gets403() throws Exception {
        String body = createQuiz(token(OWNER_ID, "INSTRUCTOR"), QUIZ_JSON)
                .andReturn().getResponse().getContentAsString();
        long id = ((Number) JsonPath.read(body, "$.id")).longValue();
        when(enrollmentClient.isEnrolled(eq(COURSE_ID), anyString())).thenReturn(false);

        mockMvc.perform(get("/api/assessments/" + id).header(HttpHeaders.AUTHORIZATION, token(3, "STUDENT")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.detail").value("You must be enrolled in the course to view its assessments"));
    }

    @Test
    void owner_readsQuiz_withoutAnyRemoteCall() throws Exception {
        String body = createQuiz(token(OWNER_ID, "INSTRUCTOR"), QUIZ_JSON)
                .andReturn().getResponse().getContentAsString();
        long id = ((Number) JsonPath.read(body, "$.id")).longValue();

        mockMvc.perform(get("/api/assessments/" + id).header(HttpHeaders.AUTHORIZATION, token(OWNER_ID, "INSTRUCTOR")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.questions[0].correctOptionIndex").value(0));
        verify(enrollmentClient, never()).isEnrolled(any(), any());
    }

    @Test
    void listForCourse_enrolledStudent_seesSummaries() throws Exception {
        createQuiz(token(OWNER_ID, "INSTRUCTOR"), QUIZ_JSON).andExpect(status().isCreated());
        when(enrollmentClient.isEnrolled(eq(COURSE_ID), anyString())).thenReturn(true);

        mockMvc.perform(get("/api/assessments").param("courseId", "6")
                        .header(HttpHeaders.AUTHORIZATION, token(3, "STUDENT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Java Basics Quiz"))
                .andExpect(jsonPath("$[0].questionCount").value(2));
    }

    @Test
    void listWithoutCourseId_returns400() throws Exception {
        mockMvc.perform(get("/api/assessments").header(HttpHeaders.AUTHORIZATION, token(3, "STUDENT")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Missing parameter"));
    }

    @Test
    void enrollmentServiceDown_returns503() throws Exception {
        when(enrollmentClient.isEnrolled(eq(COURSE_ID), anyString()))
                .thenThrow(new DependencyUnavailableException("Enrollment service", new RuntimeException()));

        mockMvc.perform(get("/api/assessments").param("courseId", "6")
                        .header(HttpHeaders.AUTHORIZATION, token(3, "STUDENT")))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    void unknownAssessment_returns404() throws Exception {
        mockMvc.perform(get("/api/assessments/999999").header(HttpHeaders.AUTHORIZATION, token(OWNER_ID, "INSTRUCTOR")))
                .andExpect(status().isNotFound());
    }
}
