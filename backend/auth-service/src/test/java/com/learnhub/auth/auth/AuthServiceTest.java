package com.learnhub.auth.auth;

import com.learnhub.auth.user.Role;
import com.learnhub.auth.user.User;
import com.learnhub.auth.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Pure unit test: no Spring, no database. Mockito supplies fake collaborators. */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    TokenService tokenService;

    @InjectMocks
    AuthService authService;

    @Test
    void register_hashesPassword_normalizesEmail_andDefaultsToStudent() {
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("HASHED");
        when(userRepository.save(any(User.class))).thenAnswer(call -> call.getArgument(0));

        UserResponse response = authService.register(
                new RegisterRequest("  Alice  ", " Alice@Example.COM ", "secret123", null));

        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(saved.capture());
        assertThat(saved.getValue().getPasswordHash()).isEqualTo("HASHED");
        assertThat(saved.getValue().getEmail()).isEqualTo("alice@example.com");
        assertThat(saved.getValue().getFullName()).isEqualTo("Alice");
        assertThat(response.role()).isEqualTo(Role.STUDENT);
    }

    @Test
    void register_rejectsDuplicateEmail() {
        when(userRepository.existsByEmail("bob@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(
                new RegisterRequest("Bob", "bob@example.com", "secret123", Role.INSTRUCTOR)))
                .isInstanceOf(EmailAlreadyRegisteredException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void login_withCorrectPassword_returnsToken() {
        User user = new User("Carol", "carol@example.com", "HASH", Role.STUDENT);
        when(userRepository.findByEmail("carol@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret123", "HASH")).thenReturn(true);
        when(tokenService.createToken(user)).thenReturn("TOKEN");
        when(tokenService.expiresInSeconds()).thenReturn(3600L);

        LoginResponse response = authService.login(new LoginRequest(" Carol@Example.com", "secret123"));

        assertThat(response.accessToken()).isEqualTo("TOKEN");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.user().email()).isEqualTo("carol@example.com");
    }

    @Test
    void login_withWrongPassword_throwsInvalidCredentials() {
        User user = new User("Carol", "carol@example.com", "HASH", Role.STUDENT);
        when(userRepository.findByEmail("carol@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "HASH")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(new LoginRequest("carol@example.com", "wrong")))
                .isInstanceOf(InvalidCredentialsException.class);
        verify(tokenService, never()).createToken(any());
    }

    @Test
    void login_withUnknownEmail_throwsInvalidCredentials() {
        when(userRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(new LoginRequest("nobody@example.com", "secret123")))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}
