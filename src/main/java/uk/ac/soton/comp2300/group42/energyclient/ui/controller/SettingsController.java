package uk.ac.soton.comp2300.group42.energyclient.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import uk.ac.soton.comp2300.group42.energyclient.data.api.ColorVision;
import uk.ac.soton.comp2300.group42.energyclient.data.api.Mode;
import uk.ac.soton.comp2300.group42.energyclient.data.api.Theme;
import uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.SettingsViewModel;

import static uk.ac.soton.comp2300.group42.energyclient.ui.util.ControllerUtils.createConverter;

public class SettingsController {
    private final SettingsViewModel vm;

    @FXML private ToggleButton largeFontToggle;
    @FXML private ComboBox<ColorVision> colorVisionComboBox;
    @FXML private ComboBox<Theme> themeComboBox;
    @FXML private ComboBox<Mode> modeComboBox;
    @FXML private ToggleButton shareLocationToggle;

    @FXML private TextField costGoalField;


    public SettingsController(SettingsViewModel vm) {
        this.vm = vm;
    }

    @FXML private void initialize() {
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

    @FXML private void toggleLargeFont() {
        throw new RuntimeException("Not implemented");
    }

    @FXML private void toggleShareLocation() {
        throw new RuntimeException("Not implemented");
    }

    @FXML private void onSetCostGoal() {
        try {
            double value = Double.parseDouble(costGoalField.getText());

            if (value <= 0) throw new NumberFormatException();

            vm.setCostGoal(value);
            costGoalField.clear();
            costGoalField.setStyle("");

        } catch (NumberFormatException e) {
            costGoalField.setStyle("-fx-border-color: red;");
        }
    }
}
