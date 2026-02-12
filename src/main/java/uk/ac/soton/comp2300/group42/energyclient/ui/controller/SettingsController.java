package uk.ac.soton.comp2300.group42.energyclient.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
// Change this to ToggleSwitch
import uk.ac.soton.comp2300.group42.energyclient.ui.view.components.ToggleSwitch;
import uk.ac.soton.comp2300.group42.energyclient.data.api.ColorVision;
import uk.ac.soton.comp2300.group42.energyclient.data.api.Mode;
import uk.ac.soton.comp2300.group42.energyclient.data.api.Theme;
import uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.SettingsViewModel;

import static uk.ac.soton.comp2300.group42.energyclient.ui.util.ControllerUtils.createConverter;

public class SettingsController {
    private final SettingsViewModel vm;

    // Updated to ToggleSwitch to match your new UI class
    @FXML private ToggleSwitch largeFontToggle;
    @FXML private ToggleSwitch shareLocationToggle;

    @FXML private ComboBox<ColorVision> colorVisionComboBox;
    @FXML private ComboBox<Theme> themeComboBox;
    @FXML private ComboBox<Mode> modeComboBox;
    @FXML private TextField costGoalField;

    public SettingsController(SettingsViewModel vm) {
        this.vm = vm;
    }

    @FXML private void initialize() {
        // Because of these bindings, the ViewModel is updated automatically
        // as soon as the switch is flipped. You don't need extra methods!
        largeFontToggle.selectedProperty().bindBidirectional(vm.getPreferences().largeFontProperty());
        shareLocationToggle.selectedProperty().bindBidirectional(vm.getPreferences().shareLocationProperty());

        themeComboBox.getItems().setAll(Theme.values());
        themeComboBox.setConverter(createConverter(Theme::getName));
        themeComboBox.valueProperty().bindBidirectional(vm.getPreferences().themeProperty());

        colorVisionComboBox.getItems().setAll(ColorVision.values());
        colorVisionComboBox.setConverter(createConverter(ColorVision::getName));
        colorVisionComboBox.valueProperty().bindBidirectional(vm.getPreferences().visionProperty());

        modeComboBox.getItems().setAll(Mode.values());
        modeComboBox.setConverter(createConverter(Mode::getName));
        modeComboBox.valueProperty().bindBidirectional(vm.getPreferences().modeProperty());
    }

    /**
     * These methods no longer throw errors.
     * You can keep them empty if your FXML still references them,
     * or remove them and the 'onAction' attribute in your FXML.
     */
    @FXML private void toggleLargeFont() {
        // Handled by bidirectional binding
    }

    @FXML private void toggleShareLocation() {
        // Handled by bidirectional binding
    }

    @FXML private void onSetCostGoal() {
        try {
            String text = costGoalField.getText().replace("£", "");
            double value = Double.parseDouble(text);
            if (value <= 0) throw new NumberFormatException();

            vm.setCostGoal(value);
            costGoalField.clear();
            costGoalField.setStyle("");
        } catch (NumberFormatException e) {
            costGoalField.setStyle("-fx-border-color: red;");
        }
    }
}