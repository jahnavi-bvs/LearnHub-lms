package com.learnhub.auth.admin;

import com.learnhub.auth.auth.UserResponse;
import com.learnhub.auth.user.Role;
import com.learnhub.auth.user.User;
import com.learnhub.auth.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AdminUserService {

    private final UserRepository userRepository;

    public AdminUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> listAll() {
        return userRepository.findAllByOrderByCreatedAtDesc().stream().map(UserResponse::from).toList();
    }

    @Transactional
    public UserResponse setActive(Long userId, boolean active, Long adminId) {
        User user = findChangeable(userId, adminId);
        user.setActive(active);
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse setRole(Long userId, Role role, Long adminId) {
        User user = findChangeable(userId, adminId);
        user.setRole(role);
        return UserResponse.from(user);
    }

    /** Admin accounts are off limits, including your own: nobody can lock the platform's admins out. */
    private User findChangeable(Long userId, Long adminId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (user.getId().equals(adminId) || user.getRole() == Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Admin accounts can't be changed here");
        }
        return user;
    }
}
