package uk.ac.soton.comp2300.group42.energyclient.presentation.controller;

import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.ColorVisionManager;
import uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.AccountSettingsViewModel;

import static uk.ac.soton.comp2300.group42.energyclient.presentation.util.ControllerUtils.setIfNew;

public class AccountSettingsController {

    private final AccountSettingsViewModel vm;

    @FXML private TextField editNameField;
    @FXML private TextField editEmailField;
    @FXML private TextField passwordField;
    @FXML private TextField confirmPasswordField;
    @FXML private Label responseLabel;

    @Inject public AccountSettingsController(AccountSettingsViewModel vm) {
        this.vm = vm;
    }

    @FXML private void initialize() {
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
                responseLabel.setText("Failed to load user data.");
                responseLabel.setStyle("-fx-text-fill: " + ColorVisionManager.getWebColor(ColorVisionManager.ColorRole.VALIDATION_ERROR) + ";");
                System.out.println("Error loading user data: " + e.getMessage());
            });
            return null;
        });
    }

    @FXML private void onUpdateAccount() {
        String name = editNameField.getText().trim();
        String email = editEmailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (!password.isEmpty() && !password.equals(confirmPassword)) {
            responseLabel.setText("Passwords do not match.");
            responseLabel.setStyle("-fx-text-fill: " + ColorVisionManager.getWebColor(ColorVisionManager.ColorRole.VALIDATION_ERROR) + ";");
            return;
        }

        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.\\w+$")) {
            responseLabel.setText("Invalid email format.");
            responseLabel.setStyle("-fx-text-fill: " + ColorVisionManager.getWebColor(ColorVisionManager.ColorRole.VALIDATION_ERROR) + ";");
            return;
        }

        if (name.isEmpty()) {
            responseLabel.setText("Name cannot be empty.");
            responseLabel.setStyle("-fx-text-fill: " + ColorVisionManager.getWebColor(ColorVisionManager.ColorRole.VALIDATION_ERROR) + ";");
            return;
        }

        vm.getUser().setName(name);
        vm.getUser().setEmail(email);
        vm.save();
        if (!password.isEmpty())
            vm.setPassword(password);

        responseLabel.setText("Account updated successfully.");
        responseLabel.setStyle("-fx-text-fill: " + ColorVisionManager.getWebColor(ColorVisionManager.ColorRole.TOGGLE_ENABLED) + ";");
    }

    @FXML private void onLogout() {
        vm.logout();
    }

    @FXML private void onDeleteAccount() {
        vm.deleteAccount();
    }
}