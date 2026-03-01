package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import com.google.inject.Inject;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;
import uk.ac.soton.comp2300.group42.energyclient.presentation.store.UserStore;

public class AccessibilitySettingsViewModel {

    private final UserStore userStore;
    private final ObservablePreferences preferences;

    @Inject public AccessibilitySettingsViewModel(UserStore userStore) {
        this.userStore = userStore;
        this.preferences = userStore.getPreferences();
    }

    public ObservablePreferences getPreferences() {
        return preferences;
    }

    public void save() {
        userStore.savePreferences();
    }
}