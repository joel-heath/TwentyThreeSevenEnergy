package uk.ac.soton.comp2300.group42.energyclient.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.Navigator;
import uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.LoginViewModel;

public class LoginController {

    @FXML private Button accessibilityButton;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    private final LoginViewModel vm;
    public LoginController(LoginViewModel vm) { this.vm = vm; }

    private boolean guard(boolean condition, String errorMessage) {
        if (condition)
            showError(errorMessage);

        return condition;
    }

    @FXML private void onLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (guard(email.isBlank(), "Email is required") ||
            guard(password.isBlank(), "Password is required"))
            return;

        boolean authenticated = vm.login(email, password);

        if (!authenticated) {
            showError("Invalid email or password.");
            return;
        }

        Navigator.goToIrreversible("dashboard.fxml");
    }

    @FXML private void goToSignUp() {
        Navigator.goTo("Register.fxml");
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML private void onGoToAccessibility() {Navigator.goTo("AccessibilitySettings.fxml");
    }
}
