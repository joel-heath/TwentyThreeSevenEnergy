package uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel;

import com.google.inject.Inject;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.PreferencesModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.IDoEverything;

public class AccessibilitySettingsViewModel {

    private final IDoEverything IDoEverything;
    private final PreferencesModel preferences;

    @Inject public AccessibilitySettingsViewModel(IDoEverything IDoEverything) {
        this.IDoEverything = IDoEverything;
        this.preferences = IDoEverything.getPreferences();
    }

    /**
     * Provides the preferences model for bidirectional binding in the controller.
     */
    public PreferencesModel getPreferences() {
        return preferences;
    }

    /**
     * Persists the preference changes to the repository.
     */
    public void save() {
        IDoEverything.savePreferences();
    }
}