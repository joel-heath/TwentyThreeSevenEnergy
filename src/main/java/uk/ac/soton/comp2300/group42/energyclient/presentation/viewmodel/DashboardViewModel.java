package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import com.google.inject.Inject;
import javafx.beans.property.ObjectProperty;
import uk.ac.soton.comp2300.group42.preferences.Mode;
import uk.ac.soton.comp2300.group42.energyclient.presentation.model.PreferencesModel;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.IDoEverything;

public class DashboardViewModel {

    private final ObjectProperty<Mode> preferredMode;

    @Inject public DashboardViewModel(IDoEverything IDoEverything) {
        PreferencesModel prefs = IDoEverything.getPreferences();
        preferredMode = prefs.modeProperty();
    }

    public Mode getPreferredMode() { return preferredMode.get(); }
}
