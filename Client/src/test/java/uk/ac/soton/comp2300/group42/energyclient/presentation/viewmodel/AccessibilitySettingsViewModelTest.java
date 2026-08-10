package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.House;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Preferences;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHouse;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;
import uk.ac.soton.comp2300.group42.preferences.ColorVision;
import uk.ac.soton.comp2300.group42.preferences.Theme;

import java.time.ZoneId;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class AccessibilitySettingsViewModelTest {

    private ObservablePreferences preferences;
    private AccessibilitySettingsViewModel viewModel;

    @BeforeEach
    void setUp() {
        ObservableHouse house = new ObservableHouse(
                new House(1L, "Home", "1 Street", ZoneId.of("UTC"), Role.OWNER)
        );
        preferences = new ObservablePreferences(new Preferences(), house);
        viewModel = new AccessibilitySettingsViewModel(preferences);
    }

    @Test
    void properties_delegateToPreferences() {
        assertSame(preferences.largeFontProperty(), viewModel.largeFontProperty());
        assertSame(preferences.themeProperty(), viewModel.themeProperty());
        assertSame(preferences.visionProperty(), viewModel.colorVisionProperty());
    }

    @Test
    void availableThemes_containsAllEnumValues() {
        assertEquals(Theme.values().length, viewModel.getAvailableThemes().size());
        assertIterableEquals(Arrays.asList(Theme.values()), viewModel.getAvailableThemes());
    }

    @Test
    void availableColorVisions_containsAllEnumValues() {
        assertEquals(ColorVision.values().length, viewModel.getAvailableColorVisions().size());
        assertIterableEquals(Arrays.asList(ColorVision.values()), viewModel.getAvailableColorVisions());
    }
}

