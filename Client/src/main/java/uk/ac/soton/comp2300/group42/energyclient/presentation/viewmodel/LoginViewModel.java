package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import com.google.inject.Inject;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.ApiException;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.AuthRepository;

public class LoginViewModel {

    private final AuthRepository repo;

    private final StringProperty email = new SimpleStringProperty("");
    private final StringProperty password = new SimpleStringProperty("");
    private final StringProperty responseMessage = new SimpleStringProperty("");
    private final BooleanProperty hasResponseError = new SimpleBooleanProperty(false);

    @Inject public LoginViewModel(AuthRepository repo) {
        this.repo = repo;
    }

    private boolean guard(boolean condition, String message) {
        if (condition)
            setResponse(message);

        return condition;
    }

    public boolean login() {
        if (guard(email.get().isBlank(), "Email is required") ||
            guard(password.get().isBlank(), "Password is required"))
            return false;

        try {
            repo.login(email.get(), password.get());
            return true;
        } catch (ApiException e) {
            setResponse(e.getMessage());
            return false;
        }
    }

    private void setResponse(String message) {
        responseMessage.set(message);
        hasResponseError.set(true);
    }

    public StringProperty emailProperty() { return email; }
    public StringProperty passwordProperty() { return password; }
    public StringProperty responseMessageProperty() { return responseMessage; }
    public BooleanProperty hasResponseErrorProperty() { return hasResponseError; }
}
