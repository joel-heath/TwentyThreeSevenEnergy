package uk.ac.soton.comp2300.group42.energyclient.ui.controller;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import uk.ac.soton.comp2300.group42.energyclient.data.api.ColorVision;
import uk.ac.soton.comp2300.group42.energyclient.ui.view.components.ToggleSwitch;
import uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.AccessibilitySettingsViewModel;

import static uk.ac.soton.comp2300.group42.energyclient.ui.util.ControllerUtils.createConverter;

public class AccessibilityController {

    @FXML private ToggleSwitch largeFontToggle;
    @FXML private ComboBox<ColorVision> colorVisionComboBox;

    private final AccessibilitySettingsViewModel vm;
    @Inject public AccessibilityController(AccessibilitySettingsViewModel vm) {
        this.vm = vm;
    }

    @FXML private void initialize() {
        largeFontToggle.selectedProperty().bindBidirectional(
                vm.getPreferences().largeFontProperty()
        );

        colorVisionComboBox.getItems().setAll(ColorVision.values());
        colorVisionComboBox.setConverter(createConverter(ColorVision::getName));

        colorVisionComboBox.valueProperty().bindBidirectional(
                vm.getPreferences().visionProperty()
        );
    }
}