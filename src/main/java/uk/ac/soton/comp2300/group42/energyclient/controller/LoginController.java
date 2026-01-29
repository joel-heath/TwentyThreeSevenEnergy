package uk.ac.soton.comp2300.group42.energyclient.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import uk.ac.soton.comp2300.group42.energyclient.util.Navigator;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private TextField passwordField;

    public void onBack() { Navigator.goBack(); }

    public void onLogin() {
        // not implemented
    }

    public void onCreateAccount() {
        Navigator.goTo("createAccount.fxml");
    }
}
