package com.learnhub.course.course;

import com.jayway.jsonpath.JsonPath;
import com.learnhub.course.config.JwtProperties;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.time.Instant;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Signs real tokens with the shared JWT_SECRET (as auth-service would), so every request
 * goes through the real JwtDecoder and role mapping. Data is rolled back after each test.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CourseControllerIntegrationTest {

    private static final String COURSE_JSON = """
            {"title": "Spring Boot Basics", "description": "Learn Spring", "published": %s}
            """;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    SecretKey jwtSigningKey;

    @Autowired
    JwtProperties jwtProperties;

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

    private long createCourse(String bearer, boolean published) throws Exception {
        String body = mockMvc.perform(post("/api/courses").header(HttpHeaders.AUTHORIZATION, bearer)
                        .contentType(MediaType.APPLICATION_JSON).content(COURSE_JSON.formatted(published)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(body, "$.id")).longValue();
    }

    @Test
    void instructor_canCreateCourse_201_withLocationAndOwner() throws Exception {
        mockMvc.perform(post("/api/courses").header(HttpHeaders.AUTHORIZATION, token(10, "INSTRUCTOR"))
                        .contentType(MediaType.APPLICATION_JSON).content(COURSE_JSON.formatted(true)))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(jsonPath("$.instructorId").value(10))
                .andExpect(jsonPath("$.instructorName").value("User 10"))
                .andExpect(jsonPath("$.published").value(true));
    }

    @Test
    void categoryAndLevel_areSaved_andReplacedByPut() throws Exception {
        String body = mockMvc.perform(post("/api/courses").header(HttpHeaders.AUTHORIZATION, token(10, "INSTRUCTOR"))
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {"title": "Physics", "category": "Physics", "level": "Beginner", "published": true}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.category").value("Physics"))
                .andExpect(jsonPath("$.level").value("Beginner"))
                .andReturn().getResponse().getContentAsString();
        long id = ((Number) JsonPath.read(body, "$.id")).longValue();

        mockMvc.perform(put("/api/courses/" + id).header(HttpHeaders.AUTHORIZATION, token(10, "INSTRUCTOR"))
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {"title": "Physics", "category": "Science", "level": "Advanced", "published": true}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category").value("Science"))
                .andExpect(jsonPath("$.level").value("Advanced"));
    }

    @Test
    void unknownLevel_returns400() throws Exception {
        mockMvc.perform(post("/api/courses").header(HttpHeaders.AUTHORIZATION, token(10, "INSTRUCTOR"))
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {"title": "Physics", "level": "Expert"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.level").value("Level must be Beginner, Intermediate or Advanced"));
    }

    @Test
    void student_cannotCreateCourse_403() throws Exception {
        mockMvc.perform(post("/api/courses").header(HttpHeaders.AUTHORIZATION, token(20, "STUDENT"))
                        .contentType(MediaType.APPLICATION_JSON).content(COURSE_JSON.formatted(true)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.title").value("Forbidden"));
    }

    @Test
    void noToken_cannotCreateCourse_401_withJsonBody() throws Exception {
        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON).content(COURSE_JSON.formatted(true)))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string(HttpHeaders.WWW_AUTHENTICATE, "Bearer"))
                .andExpect(jsonPath("$.title").value("Unauthorized"));
    }

    @Test
    void invalidBody_returns400_withFieldErrors() throws Exception {
        mockMvc.perform(post("/api/courses").header(HttpHeaders.AUTHORIZATION, token(10, "INSTRUCTOR"))
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {"title": ""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.title").value("Title is required"));
    }

    @Test
    void catalog_isPublic_andHidesDrafts() throws Exception {
        String instructor = token(10, "INSTRUCTOR");
        long publishedId = createCourse(instructor, true);
        long draftId = createCourse(instructor, false);

        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id").value(hasItem((int) publishedId)))
                .andExpect(jsonPath("$[*].id").value(not(hasItem((int) draftId))));

        // Draft: hidden from the public, visible to its owner
        mockMvc.perform(get("/api/courses/" + draftId)).andExpect(status().isNotFound());
        mockMvc.perform(get("/api/courses/" + draftId).header(HttpHeaders.AUTHORIZATION, instructor))
                .andExpect(status().isOk());

        // ...and listed under "mine"
        mockMvc.perform(get("/api/courses/mine").header(HttpHeaders.AUTHORIZATION, instructor))
                .andExpect(jsonPath("$[*].id").value(hasItem((int) draftId)));
    }

    @Test
    void owner_canUpdate_otherInstructor_cannot_adminCan() throws Exception {
        long id = createCourse(token(10, "INSTRUCTOR"), false);
        String update = """
                {"title": "Renamed", "published": true}
                """;

        mockMvc.perform(put("/api/courses/" + id).header(HttpHeaders.AUTHORIZATION, token(10, "INSTRUCTOR"))
                        .contentType(MediaType.APPLICATION_JSON).content(update))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Renamed"))
                .andExpect(jsonPath("$.published").value(true));

        mockMvc.perform(put("/api/courses/" + id).header(HttpHeaders.AUTHORIZATION, token(11, "INSTRUCTOR"))
                        .contentType(MediaType.APPLICATION_JSON).content(update))
                .andExpect(status().isForbidden());

        mockMvc.perform(put("/api/courses/" + id).header(HttpHeaders.AUTHORIZATION, token(1, "ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON).content(update))
                .andExpect(status().isOk());
    }

    @Test
    void owner_canDelete_thenCourseIsGone() throws Exception {
        String instructor = token(10, "INSTRUCTOR");
        long id = createCourse(instructor, true);

        mockMvc.perform(delete("/api/courses/" + id).header(HttpHeaders.AUTHORIZATION, token(11, "INSTRUCTOR")))
                .andExpect(status().isForbidden());
        mockMvc.perform(delete("/api/courses/" + id).header(HttpHeaders.AUTHORIZATION, instructor))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/courses/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Course not found"));
    }

    @Test
    void nonNumericId_returns400() throws Exception {
        mockMvc.perform(get("/api/courses/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid parameter"));
    }

    @Test
    void tokenSignedWithWrongKey_returns401() throws Exception {
        SecretKey otherKey = new javax.crypto.spec.SecretKeySpec(new byte[32], "HmacSHA256");
        JwtClaimsSet claims = JwtClaimsSet.builder().issuer(jwtProperties.issuer()).subject("10")
                .expiresAt(Instant.now().plusSeconds(300)).claim("role", "ADMIN").build();
        String forged = new NimbusJwtEncoder(new ImmutableSecret<>(otherKey))
                .encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
                .getTokenValue();

        mockMvc.perform(get("/api/courses/mine").header(HttpHeaders.AUTHORIZATION, "Bearer " + forged))
                .andExpect(status().isUnauthorized());
    }
}
