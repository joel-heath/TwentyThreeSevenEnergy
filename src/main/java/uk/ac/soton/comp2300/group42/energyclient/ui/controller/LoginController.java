package uk.ac.soton.comp2300.group42.energyclient.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.Navigator;
import uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.LoginViewModel;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    private final LoginViewModel vm;
    public LoginController(LoginViewModel vm) { this.vm = vm; }

    @FXML private void onLogin() {
        String userEmail = usernameField.getText();
        String userPassword = passwordField.getText();

        if (userEmail.isBlank() || userPassword.isBlank()) {
            showError("All fields are required.");
            return;
        }

        boolean authenticated = vm.login(userEmail, userPassword);

        if (!authenticated) {
            showError("Invalid email or password.");
            return;
        }

        // Successful login → dashboard
        Navigator.goToIrreversible("dashboard.fxml");
    }

    @FXML private void goToSignUp() {
        Navigator.goTo("Signup.fxml");
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
