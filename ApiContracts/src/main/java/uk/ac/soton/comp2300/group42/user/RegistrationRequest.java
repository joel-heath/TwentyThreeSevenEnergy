package uk.ac.soton.comp2300.group42.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegistrationRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Must be a valid email address")
        String email,

        @NotBlank(message = "Password is required")
        @Pattern(regexp = PasswordValidation.PASSWORD_QUALITY_REGEX, message = PasswordValidation.PASSWORD_QUALITY_MESSAGE)
        String password
) {}
