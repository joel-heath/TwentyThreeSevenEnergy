package uk.ac.soton.comp2300.group42.preferences;

public record PreferencesRequest(
        Long userId,
        Boolean largeFont,
        ColorVision vision,
        Theme theme,
        Mode mode,
        Boolean shareLocation,
        Double energyGoal,
        Long activeHouseId
) {}