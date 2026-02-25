package uk.ac.soton.comp2300.group42.house;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.ZoneId;

public record CreateHouseRequest(
        @NotBlank(message = "House name must not be blank")
        String name,

        @NotBlank(message = "House address must not be blank")
        String address,

        @NotNull(message = "Timezone must not be null")
        ZoneId timezone
) {}
