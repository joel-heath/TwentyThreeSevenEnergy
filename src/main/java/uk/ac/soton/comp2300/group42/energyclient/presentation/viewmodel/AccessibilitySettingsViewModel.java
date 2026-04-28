package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import com.google.inject.Inject;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;
import uk.ac.soton.comp2300.group42.preferences.ColorVision;
import uk.ac.soton.comp2300.group42.preferences.Theme;

public class AccessibilitySettingsViewModel {

    private final ObservablePreferences preferences;
    private final ObservableList<Theme> availableThemes;
    private final ObservableList<ColorVision> availableColorVisions;

    @Inject public AccessibilitySettingsViewModel(ObservablePreferences preferences) {
        this.preferences = preferences;
        this.availableThemes = FXCollections.observableArrayList(Theme.values());
        this.availableColorVisions = FXCollections.observableArrayList(ColorVision.values());
    }

    public BooleanProperty largeFontProperty() { return preferences.largeFontProperty(); }
    public ObjectProperty<Theme> themeProperty() { return preferences.themeProperty(); }
    public ObjectProperty<ColorVision> colorVisionProperty() { return preferences.visionProperty(); }

    public ObservableList<Theme> getAvailableThemes() { return availableThemes; }
    public ObservableList<ColorVision> getAvailableColorVisions() { return availableColorVisions; }
}