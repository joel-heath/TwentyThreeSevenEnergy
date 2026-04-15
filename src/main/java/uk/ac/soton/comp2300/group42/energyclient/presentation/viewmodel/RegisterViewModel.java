package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import com.google.inject.Inject;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.ApiException;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.AuthRepository;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.ColorVisionManager;

public class RegisterViewModel {

    private final AuthRepository repo;

    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty email = new SimpleStringProperty("");
    private final StringProperty password = new SimpleStringProperty("");
    private final StringProperty confirmPassword = new SimpleStringProperty("");
    private final StringProperty responseMessage = new SimpleStringProperty("");
    private final ObjectProperty<ColorVisionManager.ColorRole> responseColor = new SimpleObjectProperty<>(ColorVisionManager.ColorRole.WIDGET_TEXT);

    @Inject public RegisterViewModel(AuthRepository repo) {
        this.repo = repo;
    }

    public boolean register() {
        if (guard(name.get().isBlank(), "Name cannot be blank") ||
            guard(email.get().isBlank(), "Email cannot be blank") ||
            guard(password.get().isBlank(), "Password cannot be blank") ||
            guard(confirmPassword.get().isBlank(), "Must confirm password") ||
            guard(!password.get().equals(confirmPassword.get()), "Passwords do not match."))
            return false;

        try {
            repo.register(name.get(), email.get(), password.get());
            return true;
        } catch (ApiException e) {
            setResponse(e.getMessage());
            return false;
        }
    }

    private boolean guard(boolean condition, String message) {
        if (condition)
            setResponse(message);

        return condition;
    }

    private void setResponse(String message) {
        responseMessage.set(message);
        responseColor.set(ColorVisionManager.ColorRole.VALIDATION_ERROR);
    }

    public StringProperty nameProperty() { return name; }
    public StringProperty emailProperty() { return email; }
    public StringProperty passwordProperty() { return password; }
    public StringProperty confirmPasswordProperty() { return confirmPassword; }
    public StringProperty responseMessageProperty() { return responseMessage; }
    public ObjectProperty<ColorVisionManager.ColorRole> responseColorProperty() { return responseColor; }
}
