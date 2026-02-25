package uk.ac.soton.comp2300.group42.preferences;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PreferencesRequest(
        @NotNull(message = "User ID must not be null")
        Long userId,

        @NotNull(message = "Large font preference must not be null")
        Boolean largeFont,

        @NotNull(message = "Color vision preference must not be null")
        ColorVision vision,

        @NotNull(message = "Theme preference must not be null")
        Theme theme,

        @NotNull(message = "UI mode preference must not be null")
        Mode mode,

        @NotNull(message = "Location sharing preference must not be null")
        Boolean shareLocation,

        @NotNull(message = "Energy goal must not be null")
        @Min(value = 0, message = "Energy goal must be a non-negative number")
        Double energyGoal,

        @NotNull(message = "Active house ID must not be null")
        Long activeHouseId
) {}