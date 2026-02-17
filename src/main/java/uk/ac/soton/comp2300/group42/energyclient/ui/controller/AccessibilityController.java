package uk.ac.soton.comp2300.group42.energyclient.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import uk.ac.soton.comp2300.group42.energyclient.data.api.ColorVision;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.Navigator;
import uk.ac.soton.comp2300.group42.energyclient.ui.view.components.ToggleSwitch;
import uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.AccessibilitySettingsViewModel;

import static uk.ac.soton.comp2300.group42.energyclient.ui.util.ControllerUtils.createConverter;

public class AccessibilityController {
    private final AccessibilitySettingsViewModel vm;

    @FXML private ToggleSwitch largeFontToggle;
    @FXML private ComboBox<ColorVision> colorVisionComboBox;

    /**
     * Constructor used by the Navigator's controller factory.
     * @param vm The ViewModel specifically for accessibility settings.
     */
    public AccessibilityController(AccessibilitySettingsViewModel vm) {
        this.vm = vm;
    }

    @FXML
    public void initialize() {
        // Bind the Large Font Toggle to the preferences in the ViewModel
        largeFontToggle.selectedProperty().bindBidirectional(
                vm.getPreferences().largeFontProperty()
        );

        // Set up the Colorblind options ComboBox
        colorVisionComboBox.getItems().setAll(ColorVision.values());
        colorVisionComboBox.setConverter(createConverter(ColorVision::getName));

        // Bind the ComboBox selection to the vision property in the ViewModel
        colorVisionComboBox.valueProperty().bindBidirectional(
                vm.getPreferences().visionProperty()
        );
    }

    /**
     * Navigates back to the previous screen (Settings) using the Navigator's history.
     */
    @FXML
    private void onBack() {
        Navigator.goBack();
    }

    /**
     * Required stub if the FXML contains onAction="#toggleLargeFont".
     * The actual logic is handled by the bidirectional binding in initialize().
     */
    @FXML
    private void toggleLargeFont() {
        // Logic handled by binding
    }
}