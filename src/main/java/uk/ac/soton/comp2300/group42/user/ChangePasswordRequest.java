package uk.ac.soton.comp2300.group42.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ChangePasswordRequest(
        @NotBlank(message = "Old password must not be blank")
        @Pattern(regexp = PasswordValidation.PASSWORD_QUALITY_REGEX, message = PasswordValidation.PASSWORD_QUALITY_MESSAGE)
        String oldPassword,

        @NotBlank(message = "New password must not be blank")
        @Pattern(regexp = PasswordValidation.PASSWORD_QUALITY_REGEX, message = PasswordValidation.PASSWORD_QUALITY_MESSAGE)
        String newPassword
) {}
