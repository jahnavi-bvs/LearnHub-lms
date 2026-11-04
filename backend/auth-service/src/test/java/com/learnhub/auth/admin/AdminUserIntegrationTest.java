package com.learnhub.auth.admin;

import com.learnhub.auth.auth.TokenService;
import com.learnhub.auth.user.Role;
import com.learnhub.auth.user.User;
import com.learnhub.auth.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Every test's data is rolled back. */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AdminUserIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    TokenService tokenService;

    private User admin;
    private User student;

    private User create(String email, Role role) {
        return userRepository.save(new User(email, email, passwordEncoder.encode("secret123"), role));
    }

    private String bearer(User user) {
        return "Bearer " + tokenService.createToken(user);
    }

    @BeforeEach
    void createUsers() {
        admin = create("admin-test@example.com", Role.ADMIN);
        student = create("student-test@example.com", Role.STUDENT);
    }

    private ResultActions setActive(User target, boolean active) throws Exception {
        return mockMvc.perform(put("/api/auth/users/" + target.getId() + "/active")
                .header(HttpHeaders.AUTHORIZATION, bearer(admin))
                .contentType(MediaType.APPLICATION_JSON).content("{\"active\": " + active + "}"));
    }

    private ResultActions setRole(User target, String role) throws Exception {
        return mockMvc.perform(put("/api/auth/users/" + target.getId() + "/role")
                .header(HttpHeaders.AUTHORIZATION, bearer(admin))
                .contentType(MediaType.APPLICATION_JSON).content("{\"role\": \"" + role + "\"}"));
    }

    private ResultActions login(String password) throws Exception {
        return mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"student-test@example.com\", \"password\": \"" + password + "\"}"));
    }

    @Test
    void admin_listsUsers_withoutPasswords() throws Exception {
        mockMvc.perform(get("/api/auth/users").header(HttpHeaders.AUTHORIZATION, bearer(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].email", hasItem("student-test@example.com")))
                .andExpect(jsonPath("$[0].active").isBoolean())
                .andExpect(jsonPath("$[0].passwordHash").doesNotExist());
    }

    @Test
    void student_cannotUseAdminEndpoints() throws Exception {
        mockMvc.perform(get("/api/auth/users").header(HttpHeaders.AUTHORIZATION, bearer(student)))
                .andExpect(status().isForbidden());
        mockMvc.perform(put("/api/auth/users/" + student.getId() + "/role")
                        .header(HttpHeaders.AUTHORIZATION, bearer(student))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"role\": \"INSTRUCTOR\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void instructor_cannotListUsers() throws Exception {
        User instructor = create("instructor-test@example.com", Role.INSTRUCTOR);
        mockMvc.perform(get("/api/auth/users").header(HttpHeaders.AUTHORIZATION, bearer(instructor)))
                .andExpect(status().isForbidden());
    }

    @Test
    void noToken_returns401() throws Exception {
        mockMvc.perform(get("/api/auth/users")).andExpect(status().isUnauthorized());
    }

    @Test
    void newAccounts_startActive() throws Exception {
        login("secret123").andExpect(status().isOk()).andExpect(jsonPath("$.user.active").value(true));
    }

    @Test
    void deactivatedUser_cannotLogIn_untilReactivated() throws Exception {
        setActive(student, false).andExpect(status().isOk()).andExpect(jsonPath("$.active").value(false));
        login("secret123").andExpect(status().isForbidden()).andExpect(jsonPath("$.title").value("Account deactivated"));

        setActive(student, true).andExpect(status().isOk());
        login("secret123").andExpect(status().isOk());
    }

    @Test
    void deactivatedUser_withWrongPassword_learnsNothing() throws Exception {
        setActive(student, false);
        login("wrong-password").andExpect(status().isUnauthorized());
    }

    @Test
    void roleChange_appearsInTheNextLogin() throws Exception {
        setRole(student, "INSTRUCTOR").andExpect(status().isOk()).andExpect(jsonPath("$.role").value("INSTRUCTOR"));
        login("secret123").andExpect(jsonPath("$.user.role").value("INSTRUCTOR"));
    }

    @Test
    void nobodyCanBeMadeAdmin() throws Exception {
        setRole(student, "ADMIN").andExpect(status().isBadRequest());
    }

    @Test
    void adminCannotChangeThemselves() throws Exception {
        setActive(admin, false).andExpect(status().isConflict());
        setRole(admin, "STUDENT").andExpect(status().isConflict());
    }

    @Test
    void adminCannotChangeAnotherAdmin() throws Exception {
        User other = create("admin2-test@example.com", Role.ADMIN);
        setActive(other, false).andExpect(status().isConflict());
        setRole(other, "STUDENT").andExpect(status().isConflict());
    }

    @Test
    void unknownUser_returns404() throws Exception {
        mockMvc.perform(put("/api/auth/users/999999/active").header(HttpHeaders.AUTHORIZATION, bearer(admin))
                        .contentType(MediaType.APPLICATION_JSON).content("{\"active\": false}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void missingValue_returns400() throws Exception {
        mockMvc.perform(put("/api/auth/users/" + student.getId() + "/active").header(HttpHeaders.AUTHORIZATION, bearer(admin))
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }
}
