package uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel;

import uk.ac.soton.comp2300.group42.energyclient.ui.model.PreferencesModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.Repository;

public class SettingsViewModel {
    private final PreferencesModel preferences;

    public SettingsViewModel(Repository repository) {
        this.preferences = repository.getPreferences();
    }

    public PreferencesModel getPreferences() {
        return preferences;
    }

    // You can add save logic here later
    public void save() {
        // repository.savePreferences(preferences);
    }
}