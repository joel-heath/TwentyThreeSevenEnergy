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

public class RegisterController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Hyperlink loginLink;

    private final LoginViewModel vm;
    @Inject public RegisterController(LoginViewModel vm) { this.vm = vm; }

    @FXML private void initialize() {
        loginLink.textFillProperty().bind(ColorVisionManager.visionProperty().map(
                vision -> ColorVisionManager.getColor(vision, ColorVisionManager.ColorRole.TOGGLE_ENABLED)
        ));
    }

    private boolean guard(boolean condition, String errorMessage) {
        if (condition)
            showError(errorMessage);

        return condition;
    }

    @FXML private void onRegister() {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        if (guard(name.isBlank(), "Name is required") ||
            guard(email.isBlank(), "Email is required") ||
            guard(password.isBlank(), "Password is required") ||
            guard(confirm.isBlank(), "Password confirmation is required") ||
            guard(!password.equals(confirm), "Passwords do not match"))
            return;

        try {
            vm.register(name, email, password);
        }
        catch (Exception e) {
            showError("An error occurred during registration: " + e.getMessage());
            return;
        }

        Navigator.goToIrreversible("Dashboard.fxml");
    }

    @FXML private void goToLogin() {
        Navigator.goTo("Login.fxml");
    }


    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Sign Up Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
