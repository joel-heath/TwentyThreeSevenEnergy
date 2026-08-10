package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import com.google.inject.Inject;
import javafx.beans.property.*;
import uk.ac.soton.comp2300.group42.energyclient.di.qualifier.UIExecutor;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.ApiException;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.BadRequestException;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.AuthRepository;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHousemate;
import uk.ac.soton.comp2300.group42.energyclient.presentation.store.UserStore;

import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class AccountSettingsViewModel {

    public enum ConfirmAction {
        LOGOUT,
        DELETE_ACCOUNT
    }

    private ConfirmAction pendingAction;
    private Consumer<Boolean> navigationCallback;

    public void setNavigationCallback(Consumer<Boolean> callback) {
        this.navigationCallback = callback;
    }

    private final UserStore userStore;
    private final AuthRepository authRepo;
    private final Executor uiExecutor;

    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty email = new SimpleStringProperty("");
    private final StringProperty currentPassword = new SimpleStringProperty("");
    private final StringProperty newPassword = new SimpleStringProperty("");
    private final StringProperty confirmPassword = new SimpleStringProperty("");

    private final BooleanProperty isLoading = new SimpleBooleanProperty(true);
    private final StringProperty responseMessage = new SimpleStringProperty("");
    private final StringProperty responseStyleClass = new SimpleStringProperty("");

    @Inject public AccountSettingsViewModel(UserStore userStore, AuthRepository authRepo, @UIExecutor Executor uiExecutor) {
        this.userStore = userStore;
        this.authRepo = authRepo;
        this.uiExecutor = uiExecutor;
    }

    public void loadData() {
        isLoading.set(true);
        userStore.refreshAsync()
        .thenRunAsync(() -> {
            ObservableHousemate user = userStore.getCurrent();
            name.set(user.getName());
            email.set(user.getEmail());
            isLoading.set(false);
        }, uiExecutor
        ).exceptionallyAsync(e -> {
            name.set("");
            email.set("");
            setResponse("Failed to load user data.", "response-error");
            isLoading.set(false);
            System.out.println("Error loading user data: " + e.getMessage());
            return null;
        }, uiExecutor);
    }

    private boolean guard(boolean condition, String errorMessage) {
        if (condition)
            setResponse(errorMessage, "response-error");

        return condition;
    }

    public void updateName() {
        if (guard(name.get().trim().isEmpty(), "Name cannot be empty."))
            return;

        userStore.getCurrent().setName(name.get().trim());

        try {
            userStore.saveUser();
            setResponse("Name updated successfully.", "response-success");
        }
        catch (ApiException e) {
            setResponse("Failed to update name: " + e.getMessage(), "response-error");
        }
    }

    public void updateEmail() {
        if (guard(email.get().trim().isEmpty(), "Email cannot be empty.") ||
            guard(!email.get().matches("^[\\w.-]+@[\\w.-]+\\.\\w+$"), "Invalid email format."))
            return;

        userStore.getCurrent().setEmail(email.get().trim());

        try {
            userStore.saveUser();
            setResponse("Email updated successfully.", "response-success");
        }
        catch (ApiException e) {
            setResponse("Failed to update name: " + e.getMessage(), "response-error");
        }
    }

    public void updatePassword() {
        if (guard(currentPassword.get().isEmpty(), "Must enter current password to change it.") ||
            guard(newPassword.get().isEmpty(), "New password cannot be empty.") ||
            guard(!newPassword.get().equals(confirmPassword.get()), "Passwords do not match."))
            return;

        try {
            authRepo.changePassword(currentPassword.get(), newPassword.get());
            currentPassword.set("");
            newPassword.set("");
            confirmPassword.set("");
            setResponse("Account updated successfully.", "response-success");
        }
        catch (BadRequestException e) {
            String prefix = "Validation failed: newPassword: ";
            if (e.getMessage().startsWith(prefix))
                setResponse(e.getMessage().substring(prefix.length()), "response-error");
            else {
                System.err.println("Unexpected Bad Request: \n" + e.getMessage());
                setResponse("Failed to change password.", "response-error");
            }
        }
        catch (ApiException e) {
            System.err.println("Unexpected API Exception: \n" + e.getMessage());
            setResponse("Failed to change password.", "response-error");
        }
    }

    public void requestLogout(Runnable onConfirm) {
        pendingAction = ConfirmAction.LOGOUT;
        onConfirm.run();
    }

    public void requestDeleteAccount(Runnable onConfirm) {
        pendingAction = ConfirmAction.DELETE_ACCOUNT;
        onConfirm.run();
    }

    public void handleConfirmedAction() {
        if (pendingAction == null) {
            return;
        }

        switch (pendingAction) {
            case LOGOUT -> {
                logout();
                navigateToLanding();
            }
            case DELETE_ACCOUNT -> {
                if (deleteAccount()) {
                    navigateToLanding();
                }
            }
        }

        pendingAction = null;
    }

    public String getConfirmationTitle() { return pendingAction == ConfirmAction.LOGOUT ? "Logout" : "Delete Account"; }

    public String getConfirmationMessage() {
        return pendingAction == ConfirmAction.LOGOUT
                ? "Are you sure you want to log out?  You will need to sign in again."
                : "Are you sure you want to delete your account? This action cannot be undone.";
    }

    private void logout() { authRepo.logout(); }

    private boolean deleteAccount() {
        if (currentPassword.get().isEmpty()) {
            setResponse("Must enter current password to delete account.", "response-error");
            return false;
        }
        try {
            userStore.deleteUser(currentPassword.get());
        }
        catch (ApiException e) {
            setResponse("Failed to delete account: " + e.getMessage(), "response-error");
            return false;
        }
        authRepo.logout();
        return true;
    }

    private void navigateToLanding() {
        if (navigationCallback != null) {
            navigationCallback.accept(true);
        }
    }

    private void setResponse(String message, String styleClass) {
        responseMessage.set(message);
        responseStyleClass.set(styleClass);
    }

    public StringProperty nameProperty() { return name; }
    public StringProperty emailProperty() { return email; }
    public StringProperty currentPasswordProperty() { return currentPassword; }
    public StringProperty newPasswordProperty() { return newPassword; }
    public StringProperty confirmPasswordProperty() { return confirmPassword; }
    public BooleanProperty isLoadingProperty() { return isLoading; }
    public StringProperty responseMessageProperty() { return responseMessage; }
    public StringProperty responseStyleClassProperty() { return responseStyleClass; }
}
