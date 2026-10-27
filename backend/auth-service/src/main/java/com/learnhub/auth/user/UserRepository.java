package com.learnhub.auth.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data generates the implementation at startup.
 * JpaRepository already gives us save, findById, findAll, deleteById, count...
 * The methods below are "derived queries": Spring reads the method name and writes the SQL.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    // SELECT ... FROM users WHERE email = ?
    Optional<User> findByEmail(String email);

    // SELECT COUNT(*) > 0 FROM users WHERE email = ?
    boolean existsByEmail(String email);
}
