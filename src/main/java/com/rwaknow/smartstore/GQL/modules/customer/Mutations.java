package com.rwaknow.smartstore.GQL.modules.customer;

import com.rwaknow.smartstore.GQL.modules.customer.Types.*;
import com.rwaknow.smartstore.model.User;
import com.rwaknow.smartstore.repository.UserRepository;
import com.rwaknow.smartstore.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;

/**
 * Customer account mutations - profile updates, password changes
 */
@Controller("customerMutations")
@RequiredArgsConstructor
public class Mutations {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public User updateMyProfile(
            @Argument String firstName,
            @Argument String lastName,
            @Argument String email,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();

        if (firstName != null) user.setFirstName(firstName);
        if (lastName != null) user.setLastName(lastName);
        if (email != null) {
            // Check if email is already taken
            if (userRepository.existsByEmailAndIdNot(email, user.getId())) {
                throw new RuntimeException("Email already in use");
            }
            user.setEmail(email);
        }

        return userRepository.save(user);
    }

    @MutationMapping
    @PreAuthorize("isAuthenticated()")
    public PasswordChangeResponse changeMyPassword(
            @Argument String currentPassword,
            @Argument String newPassword,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();

        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return new PasswordChangeResponse(false, "Current password is incorrect");
        }

        // Validate new password
        if (newPassword.length() < 8) {
            return new PasswordChangeResponse(false, "New password must be at least 8 characters");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return new PasswordChangeResponse(true, "Password updated successfully");
    }
}