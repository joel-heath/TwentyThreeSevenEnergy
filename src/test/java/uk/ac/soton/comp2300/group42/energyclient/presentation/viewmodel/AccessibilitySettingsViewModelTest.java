package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.Preferences;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHouse;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;
import uk.ac.soton.comp2300.group42.energyclient.presentation.store.UserStore;
import uk.ac.soton.comp2300.group42.preferences.ColorVision;
import uk.ac.soton.comp2300.group42.preferences.Mode;
import uk.ac.soton.comp2300.group42.preferences.Theme;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccessibilitySettingsViewModelTest {

    @Mock private UserStore userStore;
    @Mock private ObservablePreferences preferences;
    @Mock private Preferences prefs;

    @Mock private ObservableHouse testHouse;
    @Mock private ObservableHouse testHouse2;


    @Test
    void constructor_readsPreferencesFromStore() {
        when(userStore.getPreferences()).thenReturn(preferences);

        AccessibilitySettingsViewModel viewModel = new AccessibilitySettingsViewModel(userStore);

        assertSame(preferences, viewModel.getPreferences());
    }

    @Test
    void save_delegatesToUserStore() {
        when(userStore.getPreferences()).thenReturn(preferences);
        AccessibilitySettingsViewModel viewModel = new AccessibilitySettingsViewModel(userStore);

        viewModel.save();

        verify(userStore).savePreferences();
    }

    @Test
    void constructor_readsChangedPreferencesFromStore() {
        ObservablePreferences preference = new ObservablePreferences(prefs, testHouse);

        when(userStore.getPreferences()).thenReturn(preference);
        AccessibilitySettingsViewModel viewModel = new AccessibilitySettingsViewModel(userStore);
        viewModel.save();

        assertSame(testHouse, viewModel.getPreferences().getActiveHouse());

        userStore.getPreferences().setActiveHouse(testHouse2);
        userStore.getPreferences().setEnergyGoal(1.5); // set to £1.50
        userStore.getPreferences().setMode(Mode.ADVANCED);
        userStore.getPreferences().setVision(ColorVision.PROTAN);
        userStore.getPreferences().setTheme(Theme.DARK);
        userStore.getPreferences().setLargeFont(true);
        userStore.getPreferences().setShareLocation(true);

        viewModel.save();

        assertSame(testHouse2, viewModel.getPreferences().getActiveHouse());
        assertEquals(1.5, viewModel.getPreferences().getEnergyGoal());
        assertSame(Mode.ADVANCED, viewModel.getPreferences().getMode());
        assertSame(ColorVision.PROTAN, viewModel.getPreferences().getVision());
        assertSame(Theme.DARK, viewModel.getPreferences().getTheme());
        assertSame(true, viewModel.getPreferences().getLargeFont());
        assertSame(true, viewModel.getPreferences().getShareLocation());
    }
}
