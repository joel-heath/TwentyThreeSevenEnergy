package uk.ac.soton.comp2300.group42.energyclient.presentation.controller;

import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.ColorVisionManager;
import uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.AccountSettingsViewModel;

import static uk.ac.soton.comp2300.group42.energyclient.presentation.util.ControllerUtils.setIfNew;

public class AccountSettingsController {

    private final AccountSettingsViewModel vm;

    @FXML private TextField editNameField;
    @FXML private TextField editEmailField;
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button deleteAccountButton;
    @FXML private Label responseLabel;

    @Inject public AccountSettingsController(AccountSettingsViewModel vm) {
        this.vm = vm;
    }

    private ColorVisionManager.ColorRole responseRole = ColorVisionManager.ColorRole.WIDGET_TEXT;

    @FXML private void initialize() {
        bindDeleteButtonStyle();
        bindResponseLabelColour();

        editNameField.setText(vm.getUser().getName());
        editEmailField.setText(vm.getUser().getName());
        editNameField.setDisable(true);
        editEmailField.setDisable(true);

        vm.refreshDataAsync().thenRunAsync(() ->
                Platform.runLater(() -> {
                    setIfNew(editNameField, vm.getUser().getName());
                    setIfNew(editEmailField, vm.getUser().getEmail());

                    editNameField.setDisable(false);
                    editEmailField.setDisable(false);
                })
        ).exceptionally(e -> {
            Platform.runLater(() -> {
                editNameField.setText("");
                editEmailField.setText("");
                applyResponseMessage("Failed to load user data.", ColorVisionManager.ColorRole.VALIDATION_ERROR);
                System.out.println("Error loading user data: " + e.getMessage());
            });
            return null;
        });
    }

    @FXML private void onUpdateAccount() {
        String name = editNameField.getText().trim();
        String email = editEmailField.getText().trim();

        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (!newPassword.isEmpty() && !newPassword.equals(confirmPassword)) {
            applyResponseMessage("Passwords do not match.", ColorVisionManager.ColorRole.VALIDATION_ERROR);
            return;
        }

        if (!currentPassword.isEmpty() && newPassword.isEmpty()) {
            applyResponseMessage("New password cannot be empty.", ColorVisionManager.ColorRole.VALIDATION_ERROR);
            return;
        }

        if (!newPassword.isEmpty() && currentPassword.isEmpty()) {
            applyResponseMessage("Must enter current password to change it.", ColorVisionManager.ColorRole.VALIDATION_ERROR);
            return;
        }

        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.\\w+$")) {
            applyResponseMessage("Invalid email format.", ColorVisionManager.ColorRole.VALIDATION_ERROR);
            return;
        }

        if (name.isEmpty()) {
            applyResponseMessage("Name cannot be empty.", ColorVisionManager.ColorRole.VALIDATION_ERROR);
            return;
        }

        vm.getUser().setName(name);
        vm.getUser().setEmail(email);
        vm.save();
        if (!newPassword.isEmpty())
            vm.setPassword(currentPassword, newPassword);

        applyResponseMessage("Account updated successfully.", ColorVisionManager.ColorRole.TOGGLE_ENABLED);
    }

    @FXML private void onLogout() {
        vm.logout();
    }

    @FXML private void onDeleteAccount() {
        String currentPassword = currentPasswordField.getText();

        if (currentPassword.isEmpty()) {
            applyResponseMessage("Must enter password to delete account.", ColorVisionManager.ColorRole.VALIDATION_ERROR);
            return;
        }

        vm.deleteAccount(currentPassword);
    }

    private void bindDeleteButtonStyle() {
        deleteAccountButton.styleProperty().bind(ColorVisionManager.visionProperty().map(
                vision -> "-fx-background-color: " + ColorVisionManager.getWebColor(
                        vision, ColorVisionManager.ColorRole.VALIDATION_ERROR
                ) + "; -fx-text-fill: #FFFFFF;"
        ));
    }

    private void bindResponseLabelColour() {
        updateResponseLabelColour();
        ColorVisionManager.visionProperty().addListener((_, _, _) -> updateResponseLabelColour());
    }

    private void applyResponseMessage(String message, ColorVisionManager.ColorRole role) {
        responseRole = role;
        responseLabel.setText(message);
        updateResponseLabelColour();
    }

    private void updateResponseLabelColour() {
        responseLabel.setTextFill(ColorVisionManager.getColor(responseRole));
    }
}
