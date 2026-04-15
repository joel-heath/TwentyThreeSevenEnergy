package uk.ac.soton.comp2300.group42.energyclient.presentation.controller;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import uk.ac.soton.comp2300.group42.energyclient.presentation.view.components.ToggleSwitch;
import uk.ac.soton.comp2300.group42.preferences.ColorVision;
import uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.AccessibilitySettingsViewModel;

import static uk.ac.soton.comp2300.group42.energyclient.presentation.util.ControllerUtils.createConverter;

public class AccessibilityController {

    @FXML private ToggleSwitch largeFontToggle;
    @FXML private ComboBox<ColorVision> colorVisionComboBox;

    private final AccessibilitySettingsViewModel vm;
    @Inject public AccessibilityController(AccessibilitySettingsViewModel vm) {
        this.vm = vm;
    }

    @FXML private void initialize() {
        largeFontToggle.selectedProperty().bindBidirectional(vm.largeFontProperty());

        colorVisionComboBox.setItems(vm.getAvailableColorVisions());
        colorVisionComboBox.setConverter(createConverter(ColorVision::getName));
        colorVisionComboBox.valueProperty().bindBidirectional(vm.colorVisionProperty());
    }
}