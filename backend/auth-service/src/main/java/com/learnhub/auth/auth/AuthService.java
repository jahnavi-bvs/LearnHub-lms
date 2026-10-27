package com.learnhub.auth.auth;

import com.learnhub.auth.user.Role;
import com.learnhub.auth.user.User;
import com.learnhub.auth.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

/**
 * Business rules for accounts. Controllers stay thin and call into here,
 * which keeps the rules testable without HTTP.
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructor injection: Spring passes in the beans this class needs
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        // "Alice@Example.com " and "alice@example.com" are the same account
        String email = request.email().trim().toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyRegisteredException(email);
        }

        Role role = request.role() != null ? request.role() : Role.STUDENT;
        String passwordHash = passwordEncoder.encode(request.password());

        User saved = userRepository.save(new User(request.fullName().trim(), email, passwordHash, role));
        return UserResponse.from(saved);
    }
}
