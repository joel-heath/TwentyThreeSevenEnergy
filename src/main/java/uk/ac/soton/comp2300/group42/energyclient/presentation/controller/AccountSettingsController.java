package uk.ac.soton.comp2300.group42.energyclient.presentation.controller;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.Navigator;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.StyleClassUtils;
import uk.ac.soton.comp2300.group42.energyclient.presentation.view.components.ConfirmationModal;
import uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.AccountSettingsViewModel;

public class AccountSettingsController {


    private final AccountSettingsViewModel vm;

    @FXML private TextField editNameField;
    @FXML private TextField editEmailField;
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button deleteAccountButton;
    @FXML private Label responseLabel;
    @FXML private ConfirmationModal confirmationModal;


    @Inject public AccountSettingsController(AccountSettingsViewModel vm) {
        this.vm = vm;
    }

    @FXML private void initialize() {
        editNameField.textProperty().bindBidirectional(vm.nameProperty());
        editEmailField.textProperty().bindBidirectional(vm.emailProperty());
        currentPasswordField.textProperty().bindBidirectional(vm.currentPasswordProperty());
        newPasswordField.textProperty().bindBidirectional(vm.newPasswordProperty());
        confirmPasswordField.textProperty().bindBidirectional(vm.confirmPasswordProperty());

        editNameField.disableProperty().bind(vm.isLoadingProperty());
        editEmailField.disableProperty().bind(vm.isLoadingProperty());
        responseLabel.textProperty().bind(vm.responseMessageProperty());
        StyleClassUtils.bindExclusiveClass(responseLabel, vm.responseStyleClassProperty(), "response-error", "response-success");
        StyleClassUtils.setClass(deleteAccountButton, "danger-button", true);

        vm.setNavigationCallback(shouldNavigate -> {
            if (shouldNavigate) {
                Navigator.goTo("Landing.fxml");
            }
        });

        vm.loadData();
    }

    @FXML private void onUpdateName() {
        vm.updateName();
    }

    @FXML private void onUpdateEmail() {
        vm.updateEmail();
    }

    @FXML private void onUpdatePassword() {
        vm.updatePassword();
    }

    @FXML private void onLogout() {
        vm.requestLogout(() -> confirmationModal.show(
                vm.getConfirmationTitle(),
                vm.getConfirmationMessage(),
                vm::handleConfirmedAction
        ));
    }

    @FXML private void onDeleteAccount() {
        vm.requestDeleteAccount(() -> confirmationModal.show(
                vm.getConfirmationTitle(),
                vm.getConfirmationMessage(),
                vm::handleConfirmedAction
        ));
    }

}
