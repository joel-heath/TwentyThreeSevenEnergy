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
import uk.ac.soton.comp2300.group42.energyclient.presentation.store.UserStore;

import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SettingsViewModelTest {

    @Mock private UserStore userStore;
    @Mock private SessionManager sessionManager;

    private ObservablePreferences preferences;
    private SettingsViewModel viewModel;

    @BeforeEach
    void setUp() {
        ObservableHouse house = new ObservableHouse(new House(1L, "A", "B", ZoneId.systemDefault(), Role.GUEST));
        preferences = new ObservablePreferences(new Preferences(), house);
        when(userStore.getPreferences()).thenReturn(preferences);
        viewModel = new SettingsViewModel(userStore, sessionManager);
    }

    @Test
    void constructor_usesStorePreferences() {
        assertSame(preferences, viewModel.getPreferences());
    }

    @Test
    void isLoggedIn_delegatesToSessionManager() {
        when(sessionManager.isLoggedIn()).thenReturn(true);
        assertTrue(viewModel.isLoggedIn());

        when(sessionManager.isLoggedIn()).thenReturn(false);
        assertFalse(viewModel.isLoggedIn());
    }

    @Test
    void setCostGoal_updatesUnderlyingGoalProperty() {
        viewModel.setCostGoal(4.2);

        assertEquals(4.2, preferences.getEnergyGoal(), 1e-9);
    }

    @Test
    void save_delegatesToUserStore() {
        viewModel.save();

        verify(userStore).savePreferences();
    }
}
