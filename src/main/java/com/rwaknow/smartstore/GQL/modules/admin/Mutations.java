package com.rwaknow.smartstore.GQL.modules.admin;

import com.rwaknow.smartstore.model.User;
import com.rwaknow.smartstore.model.UserRole;
import com.rwaknow.smartstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;

/**
 * Admin mutations - user management operations
 */
@Controller("adminMutations")
@RequiredArgsConstructor
public class Mutations {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ======= USER MANAGEMENT =====================================

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public User updateUserRole(
            @Argument Long userId,
            @Argument String role) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRole(UserRole.valueOf(role));
        return userRepository.save(user);
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public User toggleUserStatus(@Argument Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setActive(!user.getActive());
        return userRepository.save(user);
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Boolean deleteUser(@Argument Long userId) {
        userRepository.deleteById(userId);
        return true;
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public User resetUserPassword(
            @Argument Long userId,
            @Argument String newPassword) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }
}