package uk.ac.soton.comp2300.group42.energyclient.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import uk.ac.soton.comp2300.group42.energyclient.services.AuthService;
import uk.ac.soton.comp2300.group42.energyclient.util.Navigator;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void loginController() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isBlank() || password.isBlank()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter username and password.");
            return;
        }

        boolean authenticated = AuthService.login(username, password);

        if(!authenticated) {
            showAlert(Alert.AlertType.ERROR, "Login failed", "Invalid username or password.");
            return;
        }

        showAlert(Alert.AlertType.INFORMATION,
                "Login Successful",
                "Welcome, " + username + "!");

        Navigator.goTo("dashboard.fxml");
    }

    @FXML
    private void goToSignup() {
        Navigator.goTo("signup.fxml");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
