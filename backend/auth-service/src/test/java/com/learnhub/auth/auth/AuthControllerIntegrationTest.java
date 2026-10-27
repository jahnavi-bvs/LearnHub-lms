package com.learnhub.auth.auth;

import com.learnhub.auth.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Starts the whole application against the real learnhub_auth database and sends
 * HTTP requests through MockMvc (no real network port).
 * @Transactional rolls back every test's inserts, so the database stays clean.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerIntegrationTest {

    private static final String URL = "/api/auth/register";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Test
    void register_returns201_andNeverExposesPassword() throws Exception {
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content("""
                        {"fullName": "Test Student", "email": "Test.Student@Example.com", "password": "secret123"}
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.email").value("test.student@example.com"))
                .andExpect(jsonPath("$.role").value("STUDENT"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());

        String storedHash = userRepository.findByEmail("test.student@example.com").orElseThrow().getPasswordHash();
        assertThat(storedHash).startsWith("$2").isNotEqualTo("secret123");
    }

    @Test
    void register_sameEmailTwice_returns409() throws Exception {
        String body = """
                {"fullName": "Dup", "email": "dup@example.com", "password": "secret123"}
                """;
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Email already registered"));
    }

    @Test
    void register_invalidFields_returns400_withFieldErrors() throws Exception {
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content("""
                        {"fullName": "", "email": "not-an-email", "password": "short"}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.errors.fullName").exists())
                .andExpect(jsonPath("$.errors.email").exists())
                .andExpect(jsonPath("$.errors.password").exists());
    }

    @Test
    void register_asAdmin_isRejected() throws Exception {
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content("""
                        {"fullName": "Sneaky", "email": "sneaky@example.com", "password": "secret123", "role": "ADMIN"}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.roleAllowed").value("Role must be STUDENT or INSTRUCTOR"));
    }

    @Test
    void register_unknownRole_returns400() throws Exception {
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content("""
                        {"fullName": "X", "email": "x@example.com", "password": "secret123", "role": "TEACHER"}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Malformed request"));
    }
}
