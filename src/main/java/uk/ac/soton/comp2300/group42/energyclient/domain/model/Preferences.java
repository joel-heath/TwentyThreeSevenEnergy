package uk.ac.soton.comp2300.group42.energyclient.domain.model;

import uk.ac.soton.comp2300.group42.preferences.ColorVision;
import uk.ac.soton.comp2300.group42.preferences.Mode;
import uk.ac.soton.comp2300.group42.preferences.Theme;

public record Preferences(
        Long userId,
        Boolean largeFont,
        ColorVision vision,
        Theme theme,
        Mode mode,
        Boolean shareLocation,
        Double energyGoal,
        House activeHouse
) {}
