package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.House;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Preferences;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHouse;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;
import uk.ac.soton.comp2300.group42.preferences.Mode;

import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DashboardViewModelTest {

    private ObservablePreferences preferences;
    private DashboardViewModel viewModel;

    @BeforeEach
    void setUp() {
        ObservableHouse house = new ObservableHouse(
                new House(1L, "Home", "1 Street", ZoneId.of("UTC"), Role.OWNER)
        );
        preferences = new ObservablePreferences(new Preferences(), house);
        viewModel = new DashboardViewModel(preferences);
    }

    @Test
    void targetFxml_isSetFromInitialMode() {
        assertEquals("SimpleDashboard.fxml", viewModel.targetFxmlProperty().get());
    }

    @Test
    void targetFxml_updatesWhenModeChanges() {
        preferences.modeProperty().set(Mode.ADVANCED);
        assertEquals("AdvancedDashboard.fxml", viewModel.targetFxmlProperty().get());

        preferences.modeProperty().set(Mode.SIMPLE);
        assertEquals("SimpleDashboard.fxml", viewModel.targetFxmlProperty().get());
    }

    @Test
    void targetFxml_ignoresNullMode() {
        preferences.modeProperty().set(Mode.ADVANCED);
        preferences.modeProperty().set(null);

        assertEquals("AdvancedDashboard.fxml", viewModel.targetFxmlProperty().get());
    }
}

