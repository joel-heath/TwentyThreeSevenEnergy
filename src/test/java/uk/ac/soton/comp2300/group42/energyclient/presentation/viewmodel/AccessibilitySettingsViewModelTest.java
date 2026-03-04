package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;
import uk.ac.soton.comp2300.group42.energyclient.presentation.store.UserStore;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccessibilitySettingsViewModelTest {

    @Mock private UserStore userStore;
    @Mock private ObservablePreferences preferences;

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
}
