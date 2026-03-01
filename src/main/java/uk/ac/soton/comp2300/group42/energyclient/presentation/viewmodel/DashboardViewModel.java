package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import com.google.inject.Inject;
import javafx.beans.property.ObjectProperty;
import uk.ac.soton.comp2300.group42.preferences.Mode;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;

public class DashboardViewModel {

    private final ObjectProperty<Mode> preferredMode;

    @Inject public DashboardViewModel(ObservablePreferences preferences) {
        preferredMode = preferences.modeProperty();
    }

    public Mode getPreferredMode() {
        return preferredMode.get();
    }
}
