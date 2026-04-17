package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import com.google.inject.Inject;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;
import uk.ac.soton.comp2300.group42.preferences.ColorVision;

public class AccessibilitySettingsViewModel {

    private final ObservablePreferences preferences;
    private final ObservableList<ColorVision> availableColorVisions;

    @Inject public AccessibilitySettingsViewModel(ObservablePreferences preferences) {
        this.preferences = preferences;
        this.availableColorVisions = FXCollections.observableArrayList(ColorVision.values());
    }

    public BooleanProperty largeFontProperty() {
        return preferences.largeFontProperty();
    }

    public ObjectProperty<ColorVision> colorVisionProperty() {
        return preferences.visionProperty();
    }

    public ObservableList<ColorVision> getAvailableColorVisions() {
        return availableColorVisions;
    }
}