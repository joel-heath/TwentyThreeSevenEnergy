package uk.ac.soton.comp2300.group42.energyclient.presentation.controller;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.ColorVisionManager;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.Navigator;
import uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.LoginViewModel;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Hyperlink signUpLink;

    private final LoginViewModel vm;
    @Inject public LoginController(LoginViewModel vm) { this.vm = vm; }

    @FXML private void initialize() {
        signUpLink.textFillProperty().bind(ColorVisionManager.visionProperty().map(
                vision -> ColorVisionManager.getColor(vision, ColorVisionManager.ColorRole.TOGGLE_ENABLED)
        ));
    }

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

        try {
            vm.login(email, password);
        }
        catch (Exception e) {
            // invalid email or password ?
            showError("An error occurred during registration: " + e.getMessage());
            return;
        }

        Navigator.goToIrreversible("Dashboard.fxml");
    }

    @FXML private void goToSignUp() {
        Navigator.goTo("Register.fxml");
    }

    @FXML private void onAccessibilitySettings() {
        Navigator.goTo("AccessibilitySettings.fxml");
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
