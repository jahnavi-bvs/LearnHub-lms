package com.learnhub.auth.admin;

import com.learnhub.auth.user.Role;
import com.learnhub.auth.user.User;
import com.learnhub.auth.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Public registration can't create an admin, so the first one is created here at startup.
 * The password comes from local-secrets.properties and is stored only as a BCrypt hash.
 * An existing account with this email is never changed.
 */
@Component
public class AdminAccountInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminAccountInitializer.class);
    private static final int MIN_PASSWORD_LENGTH = 8;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String email;
    private final String password;

    public AdminAccountInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder,
                                   @Value("${learnhub.admin.email}") String email,
                                   @Value("${learnhub.admin.password}") String password) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.email = email;
        this.password = password;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.existsByEmail(email) || password.isBlank()) {
            return;
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            log.warn("ADMIN_PASSWORD is shorter than {} characters: admin account not created", MIN_PASSWORD_LENGTH);
            return;
        }
        userRepository.save(new User("Admin LearnHub", email, passwordEncoder.encode(password), Role.ADMIN));
        log.info("Created the admin account {}", email);
    }
}
