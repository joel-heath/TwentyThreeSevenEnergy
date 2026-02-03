package uk.ac.soton.comp2300.group42.energyclient.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import uk.ac.soton.comp2300.group42.energyclient.services.AuthService;
import uk.ac.soton.comp2300.group42.energyclient.util.Navigator;

public class SignupController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private void signupController() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            showAlert(Alert.AlertType.ERROR, "Error", "All fields are required.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Passwords do not match.");
            return;
        }

        boolean success = AuthService.register(username, password);

        if (!success) {
            showAlert(Alert.AlertType.ERROR, "Error", "Username already exists.");
            return;
        }

        showAlert(Alert.AlertType.INFORMATION,
                "Account Created",
                "Account created successfully. Please log in.");

        // Go back to login page
        Navigator.goTo("login.fxml");
    }

    @FXML
    private void goToLogin() {
        Navigator.goTo("login.fxml");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
