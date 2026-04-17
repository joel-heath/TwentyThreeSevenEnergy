package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import com.google.inject.Inject;
import javafx.beans.property.*;
import uk.ac.soton.comp2300.group42.energyclient.di.qualifier.UIExecutor;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.ApiException;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.BadRequestException;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.AuthRepository;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHousemate;
import uk.ac.soton.comp2300.group42.energyclient.presentation.store.UserStore;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.ColorVisionManager;

import java.util.concurrent.Executor;

public class AccountSettingsViewModel {

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
    private final ObjectProperty<ColorVisionManager.ColorRole> responseColor = new SimpleObjectProperty<>(ColorVisionManager.ColorRole.WIDGET_TEXT);

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
            setResponse("Failed to load user data.", ColorVisionManager.ColorRole.VALIDATION_ERROR);
            isLoading.set(false);
            System.out.println("Error loading user data: " + e.getMessage());
            return null;
        }, uiExecutor);
    }

    private boolean guard(boolean condition, String errorMessage) {
        if (condition)
            setResponse(errorMessage, ColorVisionManager.ColorRole.VALIDATION_ERROR);

        return condition;
    }

    public void updateName() {
        if (guard(name.get().trim().isEmpty(), "Name cannot be empty."))
            return;

        userStore.getCurrent().setName(name.get().trim());

        try {
            userStore.saveUser();
            setResponse("Name updated successfully.", ColorVisionManager.ColorRole.TOGGLE_ENABLED);
        }
        catch (ApiException e) {
            setResponse("Failed to update name: " + e.getMessage(), ColorVisionManager.ColorRole.VALIDATION_ERROR);
        }
    }

    public void updateEmail() {
        if (guard(email.get().trim().isEmpty(), "Email cannot be empty.") ||
            guard(!email.get().matches("^[\\w.-]+@[\\w.-]+\\.\\w+$"), "Invalid email format."))
            return;

        userStore.getCurrent().setEmail(email.get().trim());

        try {
            userStore.saveUser();
            setResponse("Email updated successfully.", ColorVisionManager.ColorRole.TOGGLE_ENABLED);
        }
        catch (ApiException e) {
            setResponse("Failed to update name: " + e.getMessage(), ColorVisionManager.ColorRole.VALIDATION_ERROR);
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
            setResponse("Account updated successfully.", ColorVisionManager.ColorRole.TOGGLE_ENABLED);
        }
        catch (BadRequestException e) {
            String prefix = "Validation failed: newPassword: ";
            if (e.getMessage().startsWith(prefix))
                setResponse(e.getMessage().substring(prefix.length()), ColorVisionManager.ColorRole.VALIDATION_ERROR);
            else {
                System.err.println("Unexpected Bad Request: \n" + e.getMessage());
                setResponse("Failed to change password.", ColorVisionManager.ColorRole.VALIDATION_ERROR);
            }
        }
        catch (ApiException e) {
            System.err.println("Unexpected API Exception: \n" + e.getMessage());
            setResponse("Failed to change password.", ColorVisionManager.ColorRole.VALIDATION_ERROR);
        }
    }

    public void logout() {
        authRepo.logout();
    }

    public boolean deleteAccount() {
        if (currentPassword.get().isEmpty()) {
            setResponse("Must enter password to delete account.", ColorVisionManager.ColorRole.VALIDATION_ERROR);
            return false;
        }
        try {
            userStore.deleteUser(currentPassword.get());
        }
        catch (ApiException e) {
            setResponse("Failed to delete account: " + e.getMessage(), ColorVisionManager.ColorRole.VALIDATION_ERROR);
            return false;
        }
        authRepo.logout();
        return true;
    }

    private void setResponse(String message, ColorVisionManager.ColorRole role) {
        responseMessage.set(message);
        responseColor.set(role);
    }

    public StringProperty nameProperty() { return name; }
    public StringProperty emailProperty() { return email; }
    public StringProperty currentPasswordProperty() { return currentPassword; }
    public StringProperty newPasswordProperty() { return newPassword; }
    public StringProperty confirmPasswordProperty() { return confirmPassword; }
    public BooleanProperty isLoadingProperty() { return isLoading; }
    public StringProperty responseMessageProperty() { return responseMessage; }
    public ObjectProperty<ColorVisionManager.ColorRole> responseColorProperty() { return responseColor; }
}