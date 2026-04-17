package uk.ac.soton.comp2300.group42.energyclient.presentation.controller;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.ColorVisionManager;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.Navigator;
import uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.LoginViewModel;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Hyperlink signUpLink;
    @FXML private Label responseLabel;

    private final LoginViewModel vm;
    @Inject public LoginController(LoginViewModel vm) { this.vm = vm; }

    @FXML private void initialize() {
        emailField.textProperty().bindBidirectional(vm.emailProperty());
        passwordField.textProperty().bindBidirectional(vm.passwordProperty());
        responseLabel.textProperty().bind(vm.responseMessageProperty());

        vm.responseColorProperty().subscribe((_, newVal) ->
                responseLabel.setTextFill(ColorVisionManager.getColor(newVal))
        );

        signUpLink.textFillProperty().bind(ColorVisionManager.visionProperty().map(
                vision -> ColorVisionManager.getColor(vision, ColorVisionManager.ColorRole.TOGGLE_ENABLED)
        ));
    }

    @FXML private void onLogin() {
        if (vm.login())
            Navigator.goToIrreversible("Dashboard.fxml");
    }

    @FXML private void goToSignUp() {
        Navigator.goTo("Register.fxml");
    }

    @FXML private void onAccessibilitySettings() {
        Navigator.goTo("AccessibilitySettings.fxml");
    }
}
