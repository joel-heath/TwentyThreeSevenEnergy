package uk.ac.soton.comp2300.group42.user;

import jakarta.validation.constraints.NotBlank;

public record DeleteUserRequest(
        @NotBlank(message = "Password must not be blank")
        String password
) {}
