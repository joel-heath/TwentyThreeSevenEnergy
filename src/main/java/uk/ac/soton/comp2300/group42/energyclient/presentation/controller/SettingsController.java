package uk.ac.soton.comp2300.group42.energyclient.presentation.controller;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.InputFeedbackManager;
import uk.ac.soton.comp2300.group42.energyclient.presentation.view.components.ToggleSwitch;
import uk.ac.soton.comp2300.group42.preferences.Mode;
import uk.ac.soton.comp2300.group42.preferences.Theme;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.Navigator;
import uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.SettingsViewModel;

import static uk.ac.soton.comp2300.group42.energyclient.presentation.util.ControllerUtils.createConverter;
import static uk.ac.soton.comp2300.group42.energyclient.presentation.util.ControllerUtils.setValidationError;

public class SettingsController {

    private final SettingsViewModel vm;
    private final InputFeedbackManager inputFeedbackManager;

    @FXML private Button accountSettingsButton;
    @FXML private ToggleSwitch shareLocationToggle;
    @FXML private ComboBox<Theme> themeComboBox;
    @FXML private ComboBox<Mode> modeComboBox;
    @FXML private TextField costGoalField;

    @Inject public SettingsController(SettingsViewModel vm, InputFeedbackManager inputFeedbackManager) {
        this.vm = vm;
        this.inputFeedbackManager = inputFeedbackManager;
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
        String raw = costGoalField.getText() == null ? "" : costGoalField.getText().trim();

        if (raw.isEmpty()) {
            inputFeedbackManager.showPopup(
                    "Cost goal updated",
                    "Please enter a cost goal before clicking Set Goal."
            );
            setValidationError(costGoalField, true);
            return;
        }

        try {
            String text = raw.replace("£", "").trim();
            double value = Double.parseDouble(text);
            if (value <= 0) throw new NumberFormatException();

            vm.setCostGoal(value);
            inputFeedbackManager.showPopup(
                    "Cost goal updated",
                    String.format("Your new cost goal is £%.2f.", value)
            );

            costGoalField.clear();
            setValidationError(costGoalField, false);
        } catch (NumberFormatException e) {
            inputFeedbackManager.showPopup(
                    "Goal not updated",
                    "Please enter a valid number greater than 0."
            );
            setValidationError(costGoalField, true);
        }
    }

    @FXML private void onSaveSettings() {
        vm.save();
    }
}
