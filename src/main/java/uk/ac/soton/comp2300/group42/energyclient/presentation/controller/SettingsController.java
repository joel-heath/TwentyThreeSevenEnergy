package uk.ac.soton.comp2300.group42.energyclient.presentation.controller;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import uk.ac.soton.comp2300.group42.energyclient.presentation.view.components.ToggleSwitch;
import uk.ac.soton.comp2300.group42.preferences.Mode;
import uk.ac.soton.comp2300.group42.preferences.Theme;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.ColorVisionManager;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.Navigator;
import uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.SettingsViewModel;

import static uk.ac.soton.comp2300.group42.energyclient.presentation.util.ControllerUtils.createConverter;

public class SettingsController {

    @FXML private Button accountSettingsButton;
    @FXML private ToggleSwitch shareLocationToggle;
    @FXML private ComboBox<Theme> themeComboBox;
    @FXML private ComboBox<Mode> modeComboBox;
    @FXML private TextField costGoalField;

    private final SettingsViewModel vm;

    @Inject public SettingsController(SettingsViewModel vm) {
        this.vm = vm;
    }

    @FXML private void initialize() {
        shareLocationToggle.selectedProperty().bindBidirectional(vm.shareLocationProperty());

        themeComboBox.setItems(vm.getAvailableThemes());
        themeComboBox.setConverter(createConverter(Theme::getName));
        themeComboBox.valueProperty().bindBidirectional(vm.themeProperty());

        modeComboBox.setItems(vm.getAvailableModes());
        modeComboBox.setConverter(createConverter(Mode::getName));
        modeComboBox.valueProperty().bindBidirectional(vm.modeProperty());

        costGoalField.textProperty().bindBidirectional(vm.costGoalInputProperty());

        vm.hasCostGoalErrorProperty().subscribe(hasError ->
            costGoalField.setStyle(
                hasError ? "-fx-border-color: " + ColorVisionManager.getWebColor(ColorVisionManager.ColorRole.VALIDATION_ERROR) + ";"
                         : ""
            )
        );

        accountSettingsButton.setText(vm.isLoggedIn() ? "Account Settings" : "Login");
    }

    @FXML private void onGoToAccessibility() {
        Navigator.goTo("AccessibilitySettings.fxml");
    }

    @FXML private void onAccountSettings() {
        Navigator.goTo(vm.isLoggedIn() ? "AccountSettings.fxml" : "Login.fxml");
    }

    @FXML private void onSetCostGoal() {
        vm.updateCostGoal();
    }
}