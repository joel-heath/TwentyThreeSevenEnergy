package uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel;

import uk.ac.soton.comp2300.group42.energyclient.ui.model.PreferencesModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.Repository;

public class AccessibilitySettingsViewModel {
    private final Repository repository;
    private final PreferencesModel preferences;

    public AccessibilitySettingsViewModel(Repository repository) {
        this.repository = repository;
        this.preferences = repository.getPreferences(); //
    }

    /**
     * Provides the preferences model for bidirectional binding in the controller.
     */
    public PreferencesModel getPreferences() {
        return preferences; //
    }

    /**
     * Persists the preference changes to the repository.
     */
    public void save() {
        repository.savePreferences(); //
    }
}