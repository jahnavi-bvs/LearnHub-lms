package com.learnhub.course.course;

import com.learnhub.course.config.JwtProperties;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.time.Instant;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Every test's data is rolled back. */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CourseAdminIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    SecretKey jwtSigningKey;

    @Autowired
    JwtProperties jwtProperties;

    @Autowired
    CourseRepository courseRepository;

    private Course published;
    private Course draft;

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
    void createCourses() {
        published = courseRepository.save(new Course("Admin test published", "d", null, null, true, 11L, "Ira"));
        draft = courseRepository.save(new Course("Admin test draft", "d", null, null, false, 12L, "Sam"));
    }

    @Test
    void admin_seesEveryInstructorsCourses_includingDrafts() throws Exception {
        mockMvc.perform(get("/api/courses/all").header(HttpHeaders.AUTHORIZATION, token(1, "ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].title", hasItems("Admin test published", "Admin test draft")));
    }

    @Test
    void instructor_cannotListAllCourses() throws Exception {
        mockMvc.perform(get("/api/courses/all").header(HttpHeaders.AUTHORIZATION, token(11, "INSTRUCTOR")))
                .andExpect(status().isForbidden());
    }

    @Test
    void noToken_cannotListAllCourses() throws Exception {
        mockMvc.perform(get("/api/courses/all")).andExpect(status().isUnauthorized());
    }

    @Test
    void admin_unpublishes_andTheCourseLeavesTheCatalog() throws Exception {
        mockMvc.perform(put("/api/courses/" + published.getId() + "/unpublish").header(HttpHeaders.AUTHORIZATION, token(1, "ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.published").value(false));

        mockMvc.perform(get("/api/courses"))
                .andExpect(jsonPath("$[*].title", not(hasItem("Admin test published"))));
        mockMvc.perform(get("/api/courses/" + published.getId())).andExpect(status().isNotFound());
    }

    @Test
    void courseOwner_cannotUseTheAdminUnpublish() throws Exception {
        mockMvc.perform(put("/api/courses/" + published.getId() + "/unpublish").header(HttpHeaders.AUTHORIZATION, token(11, "INSTRUCTOR")))
                .andExpect(status().isForbidden());
    }

    @Test
    void unpublishingAMissingCourse_returns404() throws Exception {
        mockMvc.perform(put("/api/courses/999999/unpublish").header(HttpHeaders.AUTHORIZATION, token(1, "ADMIN")))
                .andExpect(status().isNotFound());
    }
}
