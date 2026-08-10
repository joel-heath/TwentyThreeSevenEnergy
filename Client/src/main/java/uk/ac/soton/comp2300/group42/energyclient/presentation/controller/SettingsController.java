package uk.ac.soton.comp2300.group42.energyclient.presentation.controller;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import uk.ac.soton.comp2300.group42.energyclient.presentation.view.components.AlertModal;
import uk.ac.soton.comp2300.group42.energyclient.presentation.view.components.ToggleSwitch;
import uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.AccessibilitySettingsViewModel;
import uk.ac.soton.comp2300.group42.preferences.ColorVision;
import uk.ac.soton.comp2300.group42.preferences.Mode;
import uk.ac.soton.comp2300.group42.preferences.Theme;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.Navigator;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.StyleClassUtils;
import uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.SettingsViewModel;

import static uk.ac.soton.comp2300.group42.energyclient.presentation.util.ControllerUtils.createConverter;

public class SettingsController {

    @FXML private Button accountSettingsButton;
    @FXML private ToggleSwitch shareLocationToggle;
    @FXML private ComboBox<Theme> themeComboBox;
    @FXML private ComboBox<ColorVision> colorVisionComboBox;
    @FXML private ComboBox<Mode> modeComboBox;
    @FXML private TextField costGoalField;
    @FXML private AlertModal alertModal;

    private final SettingsViewModel vm;
    private final AccessibilitySettingsViewModel accVm;

    @Inject public SettingsController(SettingsViewModel vm, AccessibilitySettingsViewModel accVm) {
        this.vm = vm;
        this.accVm = accVm;
    }

    @FXML private void initialize() {
        shareLocationToggle.selectedProperty().bindBidirectional(vm.shareLocationProperty());

        themeComboBox.setItems(accVm.getAvailableThemes());
        themeComboBox.setConverter(createConverter(Theme::getName));
        themeComboBox.valueProperty().bindBidirectional(accVm.themeProperty());

        colorVisionComboBox.setItems(accVm.getAvailableColorVisions());
        colorVisionComboBox.setConverter(createConverter(ColorVision::getName));
        colorVisionComboBox.valueProperty().bindBidirectional(accVm.colorVisionProperty());

        modeComboBox.setItems(vm.getAvailableModes());
        modeComboBox.setConverter(createConverter(Mode::getName));
        modeComboBox.valueProperty().bindBidirectional(vm.modeProperty());

        costGoalField.textProperty().bindBidirectional(vm.costGoalInputProperty());
        StyleClassUtils.bindBooleanClass(costGoalField, vm.hasCostGoalErrorProperty(), "validation-error");

        vm.setAlertCallback((title, message) -> alertModal.show(title, message));

        accountSettingsButton.setText(vm.isLoggedIn() ? "Account Settings" : "Login");
    }

    @FXML private void onAccountSettings() {
        Navigator.goTo(vm.isLoggedIn() ? "AccountSettings.fxml" : "Login.fxml");
    }

    @FXML private void onSetCostGoal() {
        vm.updateCostGoal();
    }
}
