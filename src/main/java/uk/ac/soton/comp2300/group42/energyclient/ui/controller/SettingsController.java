package uk.ac.soton.comp2300.group42.energyclient.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToggleButton;
import uk.ac.soton.comp2300.group42.energyclient.data.api.ColorVision;
import uk.ac.soton.comp2300.group42.energyclient.data.api.Mode;
import uk.ac.soton.comp2300.group42.energyclient.data.api.Theme;
import uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.SettingsViewModel;

public class SettingsController {
    private final SettingsViewModel vm;

    @FXML private ToggleButton largeFontToggle;
    @FXML private ComboBox<ColorVision> colorVisionComboBox;
    @FXML private ComboBox<Theme> themeComboBox;
    @FXML private ComboBox<Mode> modeComboBox;
    @FXML private ToggleButton shareLocationToggle;

    public SettingsController(SettingsViewModel vm) {
        this.vm = vm;
    }

    @FXML private void initialize() {
        largeFontToggle.selectedProperty().bindBidirectional(vm.getPreferences().largeFontProperty());
        shareLocationToggle.selectedProperty().bindBidirectional(vm.getPreferences().shareLocationProperty());
        themeComboBox.getItems().setAll(Theme.values());
        themeComboBox.valueProperty().bindBidirectional(vm.getPreferences().themeProperty());
        colorVisionComboBox.getItems().setAll(ColorVision.values());
        colorVisionComboBox.valueProperty().bindBidirectional(vm.getPreferences().visionProperty());
        modeComboBox.getItems().setAll(Mode.values());
        modeComboBox.valueProperty().bindBidirectional(vm.getPreferences().modeProperty());
    }

    @FXML private void toggleLargeFont() {
        throw new RuntimeException("Not implemented");
    }

    @FXML private void toggleShareLocation() {
        throw new RuntimeException("Not implemented");
    }
}
