package uk.ac.soton.comp2300.group42.energyclient.ui.controller;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.Navigator;
import uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.LoginViewModel;

public class RegisterController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    private final LoginViewModel vm;
    @Inject public RegisterController(LoginViewModel vm) { this.vm = vm; }

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

        boolean success = vm.register(name, email, password);

        if (!success) {
            showError("User already exists.");
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
