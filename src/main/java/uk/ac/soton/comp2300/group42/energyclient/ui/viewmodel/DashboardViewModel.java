package uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel;

import com.google.inject.Inject;
import javafx.beans.property.ObjectProperty;
import uk.ac.soton.comp2300.group42.energyclient.data.api.Mode;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.PreferencesModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.Repository;

public class DashboardViewModel {

    private final ObjectProperty<Mode> preferredMode;

    @Inject public DashboardViewModel(Repository repository) {
        PreferencesModel prefs = repository.getPreferences();
        preferredMode = prefs.modeProperty();
    }

    public Mode getPreferredMode() { return preferredMode.get(); }
}
