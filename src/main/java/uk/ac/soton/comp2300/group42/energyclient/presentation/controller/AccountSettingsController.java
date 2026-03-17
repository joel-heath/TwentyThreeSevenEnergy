package uk.ac.soton.comp2300.group42.energyclient.presentation.controller;

import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.ApiException;
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

    private static final String RESPONSE_ERROR_CLASS = "response-error";
    private static final String RESPONSE_SUCCESS_CLASS = "response-success";

    @FXML private void initialize() {
        deleteAccountButton.getStyleClass().add("danger-button");

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
                applyResponseMessage("Failed to load user data.", true);
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
            applyResponseMessage("Passwords do not match.", true);
            return;
        }

        if (!currentPassword.isEmpty() && newPassword.isEmpty()) {
            applyResponseMessage("New password cannot be empty.", true);
            return;
        }

        if (!newPassword.isEmpty() && currentPassword.isEmpty()) {
            applyResponseMessage("Must enter current password to change it.", true);
            return;
        }

        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.\\w+$")) {
            applyResponseMessage("Invalid email format.", true);
            return;
        }

        if (name.isEmpty()) {
            applyResponseMessage("Name cannot be empty.", true);
            return;
        }

        vm.getUser().setName(name);
        vm.getUser().setEmail(email);
        vm.save();

        if (!newPassword.isEmpty()) {
            try {
                vm.setPassword(currentPassword, newPassword);
            }
            catch (ApiException e) {
                applyResponseMessage("Failed to change password: " + e.getMessage(), true);
                return;
            }
        }

        applyResponseMessage("Account updated successfully.", false);
    }

    @FXML private void onLogout() {
        vm.logout();
    }

    @FXML private void onDeleteAccount() {
        String currentPassword = currentPasswordField.getText();

        if (currentPassword.isEmpty()) {
            applyResponseMessage("Must enter password to delete account.", true);
            return;
        }

        vm.deleteAccount(currentPassword);
    }

    private void applyResponseMessage(String message, boolean isError) {
        responseLabel.setText(message);
        var classes = responseLabel.getStyleClass();
        classes.remove(RESPONSE_ERROR_CLASS);
        classes.remove(RESPONSE_SUCCESS_CLASS);
        classes.add(isError ? RESPONSE_ERROR_CLASS : RESPONSE_SUCCESS_CLASS);
    }
}
