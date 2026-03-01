package uk.ac.soton.comp2300.group42.energyclient.presentation.controller;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import uk.ac.soton.comp2300.group42.energyclient.presentation.view.components.ToggleSwitch;
import uk.ac.soton.comp2300.group42.preferences.Mode;
import uk.ac.soton.comp2300.group42.preferences.Theme;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ColorVisionManager;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.Navigator;
import uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.SettingsViewModel;

import static uk.ac.soton.comp2300.group42.energyclient.presentation.util.ControllerUtils.createConverter;

public class SettingsController {

    private final SettingsViewModel vm;

    @FXML private Button accountSettingsButton;
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

        accountSettingsButton.setText(vm.isLoggedIn() ? "Account Settings" : "Login");
    }

    @FXML private void onGoToAccessibility() {
        Navigator.goTo("AccessibilitySettings.fxml");
    }

    @FXML private void onAccountSettings() {
        Navigator.goTo(vm.isLoggedIn() ? "AccountSettings.fxml" : "Login.fxml");
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
            costGoalField.setStyle(
                    "-fx-border-color: " + ColorVisionManager.getWebColor(ColorVisionManager.ColorRole.VALIDATION_ERROR) + ";"
            );
        }
    }
}