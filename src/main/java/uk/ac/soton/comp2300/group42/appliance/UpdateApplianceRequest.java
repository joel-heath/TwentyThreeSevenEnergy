package uk.ac.soton.comp2300.group42.appliance;

import jakarta.validation.constraints.NotBlank;

public record UpdateApplianceRequest(
        @NotBlank(message = "Appliance name must not be blank")
        String name
) {}
