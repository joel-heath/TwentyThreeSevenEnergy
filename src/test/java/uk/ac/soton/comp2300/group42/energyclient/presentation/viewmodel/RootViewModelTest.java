package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.House;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Preferences;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHouse;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;

import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RootViewModelTest {

    private ObservablePreferences preferences;
    private RootViewModel viewModel;

    @BeforeEach
    void setUp() {
        ObservableHouse house = new ObservableHouse(
            new House(1L, "Home", "1 Street", ZoneId.of("UTC"), Role.OWNER)
        );
        preferences = new ObservablePreferences(new Preferences(), house);
        viewModel = new RootViewModel(preferences);
    }

    @Test
    void themeAndVisionProperties_delegateToPreferences() {
        assertSame(preferences.themeProperty(), viewModel.themeProperty());
        assertSame(preferences.visionProperty(), viewModel.visionProperty());
    }

    @Test
    void popupNotification_canBeDismissed() {
        viewModel.showPopup("Title", "Description");

        assertEquals(1, viewModel.getActiveNotifications().size());
        RootViewModel.Notification notification = viewModel.getActiveNotifications().getFirst();
        viewModel.dismissNotification(notification);
        assertTrue(viewModel.getActiveNotifications().isEmpty());
    }

    @Test
    void showReminderAndClearAll_manageNotificationList() {
        viewModel.showReminder("Kettle");

        assertEquals(1, viewModel.getActiveNotifications().size());
        assertTrue(viewModel.getActiveNotifications().getFirst().title().contains("Kettle"));

        viewModel.clearAllNotifications();
        assertTrue(viewModel.getActiveNotifications().isEmpty());
    }
}
