package com.learnhub.course.module;

import com.jayway.jsonpath.JsonPath;
import com.learnhub.course.client.DependencyUnavailableException;
import com.learnhub.course.client.EnrollmentClient;
import com.learnhub.course.config.JwtProperties;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
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
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Real database and JWT checks; enrollment-service is replaced by a mock. Data is rolled back. */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CourseModuleIntegrationTest {

    private static final long OWNER = 10;
    private static final long STUDENT = 3;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    SecretKey jwtSigningKey;

    @Autowired
    JwtProperties jwtProperties;

    @MockitoBean
    EnrollmentClient enrollmentClient;

    private long courseId;

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
    void createCourse() throws Exception {
        courseId = idOf(mockMvc.perform(post("/api/courses").header(HttpHeaders.AUTHORIZATION, token(OWNER, "INSTRUCTOR"))
                .contentType(MediaType.APPLICATION_JSON).content("""
                        {"title": "Module Test Course", "published": true}
                        """)));
    }

    private long idOf(ResultActions result) throws Exception {
        String body = result.andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(body, "$.id")).longValue();
    }

    private ResultActions addModule(String json) throws Exception {
        return mockMvc.perform(post("/api/courses/" + courseId + "/modules")
                .header(HttpHeaders.AUTHORIZATION, token(OWNER, "INSTRUCTOR"))
                .contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private long addTitled(String title) throws Exception {
        return idOf(addModule("""
                {"title": "%s", "youtubeUrl": "https://youtu.be/dQw4w9WgXcQ", "notes": "### Notes for %s"}
                """.formatted(title, title)));
    }

    @Test
    void numberingStartsAtOne_andOutlineIsPublic_withoutContent() throws Exception {
        addTitled("First");
        addTitled("Second");

        mockMvc.perform(get("/api/courses/" + courseId + "/modules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].position").value(1))
                .andExpect(jsonPath("$[1].position").value(2))
                .andExpect(jsonPath("$[0].hasVideo").value(true))
                .andExpect(jsonPath("$[0].notes").doesNotExist())
                .andExpect(jsonPath("$[0].youtubeVideoId").doesNotExist());
    }

    @Test
    void pastedLink_isStoredAsBareVideoId() throws Exception {
        addModule("""
                {"title": "Video", "youtubeUrl": "https://www.youtube.com/watch?v=dQw4w9WgXcQ&t=5s"}
                """)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.youtubeVideoId").value("dQw4w9WgXcQ"));
    }

    @Test
    void videoIsOptional() throws Exception {
        addModule("""
                {"title": "No video yet"}
                """)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.youtubeVideoId").doesNotExist());
    }

    @Test
    void invalidVideoLink_returns400() throws Exception {
        addModule("""
                {"title": "Bad", "youtubeUrl": "https://www.youtube.com/results?search_query=java"}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.youtubeUrlValid").value("Enter a valid YouTube video link"));
    }

    @Test
    void duplicateTitleInSameCourse_returns409() throws Exception {
        addTitled("Same");
        addModule("""
                {"title": "Same"}
                """)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Duplicate module"));
    }

    @Test
    void enrolledStudent_getsVideoAndNotes() throws Exception {
        long id = addTitled("Lesson");
        when(enrollmentClient.isEnrolled(eq(courseId), anyString())).thenReturn(true);

        mockMvc.perform(get("/api/courses/" + courseId + "/modules/" + id)
                        .header(HttpHeaders.AUTHORIZATION, token(STUDENT, "STUDENT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.youtubeVideoId").value("dQw4w9WgXcQ"))
                .andExpect(jsonPath("$.notes").value("### Notes for Lesson"));
    }

    @Test
    void notEnrolledStudent_gets403_andOwnerGetsContentWithoutEnrolling() throws Exception {
        long id = addTitled("Lesson");
        when(enrollmentClient.isEnrolled(eq(courseId), anyString())).thenReturn(false);

        mockMvc.perform(get("/api/courses/" + courseId + "/modules/" + id)
                        .header(HttpHeaders.AUTHORIZATION, token(STUDENT, "STUDENT")))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/courses/" + courseId + "/modules/" + id)
                        .header(HttpHeaders.AUTHORIZATION, token(OWNER, "INSTRUCTOR")))
                .andExpect(status().isOk());
    }

    @Test
    void contentWithoutToken_returns401() throws Exception {
        long id = addTitled("Lesson");

        mockMvc.perform(get("/api/courses/" + courseId + "/modules/" + id))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void enrollmentServiceDown_returns503() throws Exception {
        long id = addTitled("Lesson");
        when(enrollmentClient.isEnrolled(eq(courseId), anyString()))
                .thenThrow(new DependencyUnavailableException("Enrollment service", new RuntimeException()));

        mockMvc.perform(get("/api/courses/" + courseId + "/modules/" + id)
                        .header(HttpHeaders.AUTHORIZATION, token(STUDENT, "STUDENT")))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    void moduleCannotBeReachedThroughAnotherCourse() throws Exception {
        long id = addTitled("Lesson");
        long other = idOf(mockMvc.perform(post("/api/courses").header(HttpHeaders.AUTHORIZATION, token(OWNER, "INSTRUCTOR"))
                .contentType(MediaType.APPLICATION_JSON).content("""
                        {"title": "Other Course", "published": true}
                        """)));

        mockMvc.perform(get("/api/courses/" + other + "/modules/" + id)
                        .header(HttpHeaders.AUTHORIZATION, token(OWNER, "INSTRUCTOR")))
                .andExpect(status().isNotFound());
    }

    @Test
    void otherInstructor_cannotChangeModules() throws Exception {
        long id = addTitled("Lesson");

        mockMvc.perform(put("/api/courses/" + courseId + "/modules/" + id)
                        .header(HttpHeaders.AUTHORIZATION, token(11, "INSTRUCTOR"))
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {"title": "Hacked"}
                                """))
                .andExpect(status().isForbidden());
        mockMvc.perform(delete("/api/courses/" + courseId + "/modules/" + id)
                        .header(HttpHeaders.AUTHORIZATION, token(3, "STUDENT")))
                .andExpect(status().isForbidden());
    }

    @Test
    void owner_canEdit() throws Exception {
        long id = addTitled("Old");

        mockMvc.perform(put("/api/courses/" + courseId + "/modules/" + id)
                        .header(HttpHeaders.AUTHORIZATION, token(OWNER, "INSTRUCTOR"))
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {"title": "New", "description": "Updated", "notes": "Changed"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New"))
                .andExpect(jsonPath("$.description").value("Updated"))
                .andExpect(jsonPath("$.youtubeVideoId").doesNotExist());
    }

    @Test
    void deleting_renumbersTheRest() throws Exception {
        addTitled("One");
        long two = addTitled("Two");
        addTitled("Three");

        mockMvc.perform(delete("/api/courses/" + courseId + "/modules/" + two)
                        .header(HttpHeaders.AUTHORIZATION, token(OWNER, "INSTRUCTOR")))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/courses/" + courseId + "/modules"))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[1].title").value("Three"))
                .andExpect(jsonPath("$[1].position").value(2));
    }

    @Test
    void reorder_changesPositions_andRejectsPartialLists() throws Exception {
        long a = addTitled("A");
        long b = addTitled("B");

        mockMvc.perform(put("/api/courses/" + courseId + "/modules/order")
                        .header(HttpHeaders.AUTHORIZATION, token(OWNER, "INSTRUCTOR"))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"moduleIds\": [" + a + "]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid module order"));

        mockMvc.perform(put("/api/courses/" + courseId + "/modules/order")
                        .header(HttpHeaders.AUTHORIZATION, token(OWNER, "INSTRUCTOR"))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"moduleIds\": [" + b + ", " + a + "]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("B"))
                .andExpect(jsonPath("$[0].position").value(1));
    }

    @Test
    void deletingCourse_removesItsModules() throws Exception {
        addTitled("Lesson");

        mockMvc.perform(delete("/api/courses/" + courseId).header(HttpHeaders.AUTHORIZATION, token(OWNER, "INSTRUCTOR")))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/courses/" + courseId + "/modules"))
                .andExpect(status().isNotFound());
    }
}
