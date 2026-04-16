package com.rwaknow.smartstore.GQL.modules.customer;

/**
 * Customer-specific types for account management
 */
public class Types {

    public record UpdateProfileInput(
            String firstName,
            String lastName,
            String email
    ) {}

    public record ChangePasswordInput(
            String currentPassword,
            String newPassword
    ) {}

    public record PasswordChangeResponse(
            boolean success,
            String message
    ) {}
}