package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.House;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Preferences;
import uk.ac.soton.comp2300.group42.energyclient.domain.session.SessionManager;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHouse;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.InputFeedbackManager;
import uk.ac.soton.comp2300.group42.preferences.Mode;
import uk.ac.soton.comp2300.group42.preferences.Theme;

import java.time.ZoneId;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SettingsViewModelTest {

    @Mock private InputFeedbackManager inputFeedbackManager;

    private ObservablePreferences preferences;
    private SessionManager sessionManager;
    private SettingsViewModel viewModel;

    @BeforeEach
    void setUp() {
        ObservableHouse house = new ObservableHouse(
                new House(1L, "Home", "1 Street", ZoneId.of("UTC"), Role.OWNER)
        );
        preferences = new ObservablePreferences(new Preferences(), house);
        sessionManager = new SessionManager();
        viewModel = new SettingsViewModel(preferences, sessionManager, inputFeedbackManager);
    }

    @Test
    void availableValues_containAllThemesAndModes() {
        assertIterableEquals(Arrays.asList(Theme.values()), viewModel.getAvailableThemes());
        assertIterableEquals(Arrays.asList(Mode.values()), viewModel.getAvailableModes());
    }

    @Test
    void properties_delegateToPreferences() {
        assertSame(preferences.shareLocationProperty(), viewModel.shareLocationProperty());
        assertSame(preferences.themeProperty(), viewModel.themeProperty());
        assertSame(preferences.modeProperty(), viewModel.modeProperty());
    }

    @Test
    void updateCostGoal_whenInputEmpty_setsErrorAndShowsFeedback() {
        viewModel.costGoalInputProperty().set("   ");

        viewModel.updateCostGoal();

        assertTrue(viewModel.hasCostGoalErrorProperty().get());
        verify(inputFeedbackManager).showPopup("Cost goal not updated", "Please enter a cost goal before clicking Set Goal.");
    }

    @Test
    void updateCostGoal_whenInputInvalid_setsErrorAndShowsFeedback() {
        viewModel.costGoalInputProperty().set("not-a-number");

        viewModel.updateCostGoal();

        assertTrue(viewModel.hasCostGoalErrorProperty().get());
        verify(inputFeedbackManager).showPopup("Cost goal not updated", "Please enter a valid number greater than 0.");
    }

    @Test
    void updateCostGoal_whenValid_updatesPreferenceAndClearsInput() {
        viewModel.costGoalInputProperty().set("2.5");

        viewModel.updateCostGoal();

        assertEquals(2.5, preferences.energyGoalProperty().get(), 1e-9);
        assertFalse(viewModel.hasCostGoalErrorProperty().get());
        assertEquals("", viewModel.costGoalInputProperty().get());
        verify(inputFeedbackManager).showPopup("Cost goal updated", "Your new cost goal is £2.50.");
    }

    @Test
    void isLoggedIn_delegatesToSessionManager() {
        assertFalse(viewModel.isLoggedIn());
        sessionManager.setLoggedIn(true);
        assertTrue(viewModel.isLoggedIn());
    }
}
