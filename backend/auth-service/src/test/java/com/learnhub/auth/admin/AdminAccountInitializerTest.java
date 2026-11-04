package com.learnhub.auth.admin;

import com.learnhub.auth.user.Role;
import com.learnhub.auth.user.User;
import com.learnhub.auth.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminAccountInitializerTest {

    private static final String EMAIL = "admin@example.com";

    private UserRepository repository;
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        repository = mock(UserRepository.class);
    }

    private void runWith(String password) {
        new AdminAccountInitializer(repository, encoder, EMAIL, password).run(null);
    }

    @Test
    void createsAdmin_withHashedPassword() {
        runWith("a-long-password");

        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
        verify(repository).save(saved.capture());
        assertThat(saved.getValue().getRole()).isEqualTo(Role.ADMIN);
        assertThat(saved.getValue().getPasswordHash()).isNotEqualTo("a-long-password");
        assertThat(encoder.matches("a-long-password", saved.getValue().getPasswordHash())).isTrue();
    }

    @Test
    void noPassword_createsNothing() {
        runWith("");
        verify(repository, never()).save(any());
    }

    @Test
    void shortPassword_createsNothing() {
        runWith("short");
        verify(repository, never()).save(any());
    }

    @Test
    void existingAccount_isLeftAlone() {
        when(repository.existsByEmail(EMAIL)).thenReturn(true);
        runWith("a-long-password");
        verify(repository, never()).save(any());
    }
}
