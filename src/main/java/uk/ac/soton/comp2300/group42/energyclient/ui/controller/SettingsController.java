package uk.ac.soton.comp2300.group42.energyclient.ui.controller;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import uk.ac.soton.comp2300.group42.energyclient.ui.view.components.ToggleSwitch;
import uk.ac.soton.comp2300.group42.energyclient.data.api.Mode;
import uk.ac.soton.comp2300.group42.energyclient.data.api.Theme;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.Navigator;
import uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.SettingsViewModel;

import static uk.ac.soton.comp2300.group42.energyclient.ui.util.ControllerUtils.createConverter;

public class SettingsController {
    private final SettingsViewModel vm;

    @FXML private ToggleSwitch shareLocationToggle;
    @FXML private ComboBox<Theme> themeComboBox;
    @FXML private ComboBox<Mode> modeComboBox;
    @FXML private TextField costGoalField;

    @Inject public SettingsController(SettingsViewModel vm) {
        this.vm = vm;
    }

    @FXML private void initialize() {
        shareLocationToggle.selectedProperty().bindBidirectional(vm.getPreferences().shareLocationProperty());

        themeComboBox.getItems().setAll(Theme.values());
        themeComboBox.setConverter(createConverter(Theme::getName));
        themeComboBox.valueProperty().bindBidirectional(vm.getPreferences().themeProperty());

        modeComboBox.getItems().setAll(Mode.values());
        modeComboBox.setConverter(createConverter(Mode::getName));
        modeComboBox.valueProperty().bindBidirectional(vm.getPreferences().modeProperty());
    }

    @FXML private void onGoToAccessibility() {
        Navigator.goTo("AccessibilitySettings.fxml");
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