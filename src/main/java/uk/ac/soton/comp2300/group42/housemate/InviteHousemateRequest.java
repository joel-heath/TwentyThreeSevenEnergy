package uk.ac.soton.comp2300.group42.housemate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import uk.ac.soton.comp2300.group42.common.Role;

public record InviteHousemateRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Must be a valid email address")
        String email,

        @NotNull(message = "Role is required")
        Role role
) {}