package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import com.google.inject.Inject;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import uk.ac.soton.comp2300.group42.preferences.Mode;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;

public class DashboardViewModel {

    private final StringProperty targetFxml = new SimpleStringProperty("");

    @Inject public DashboardViewModel(ObservablePreferences preferences) {
        updateTargetFxml(preferences.getMode());

        preferences.modeProperty().subscribe(this::updateTargetFxml);
    }

    private void updateTargetFxml(Mode mode) {
        if (mode == null) return;

        targetFxml.set(switch (mode) {
            case SIMPLE -> "SimpleDashboard.fxml";
            case ADVANCED -> "AdvancedDashboard.fxml";
        });
    }

    public ReadOnlyStringProperty targetFxmlProperty() {
        return targetFxml;
    }
}
