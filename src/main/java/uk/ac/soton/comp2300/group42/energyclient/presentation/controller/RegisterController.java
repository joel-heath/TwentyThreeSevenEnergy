package uk.ac.soton.comp2300.group42.energyclient.presentation.controller;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.ColorVisionManager;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.Navigator;
import uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.RegisterViewModel;

public class RegisterController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Hyperlink loginLink;
    @FXML private Label responseLabel;

    private final RegisterViewModel vm;
    @Inject public RegisterController(RegisterViewModel vm) { this.vm = vm; }

    @FXML private void initialize() {
        nameField.textProperty().bindBidirectional(vm.nameProperty());
        emailField.textProperty().bindBidirectional(vm.emailProperty());
        passwordField.textProperty().bindBidirectional(vm.passwordProperty());
        confirmPasswordField.textProperty().bindBidirectional(vm.confirmPasswordProperty());
        responseLabel.textProperty().bind(vm.responseMessageProperty());

        vm.responseColorProperty().subscribe((_, newVal) ->
                responseLabel.setTextFill(ColorVisionManager.getColor(newVal))
        );

        loginLink.textFillProperty().bind(ColorVisionManager.visionProperty().map(
                vision -> ColorVisionManager.getColor(vision, ColorVisionManager.ColorRole.TOGGLE_ENABLED)
        ));
    }

    @FXML private void onRegister() {
        if (vm.register())
            Navigator.goToIrreversible("Dashboard.fxml");
    }

    @FXML private void goToLogin() {
        Navigator.goTo("Login.fxml");
    }

    @FXML private void onAccessibilitySettings() {
        Navigator.goTo("AccessibilitySettings.fxml");
    }
}
