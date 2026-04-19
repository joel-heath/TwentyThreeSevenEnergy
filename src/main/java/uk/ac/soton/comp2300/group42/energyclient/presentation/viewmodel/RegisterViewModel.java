package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import com.google.inject.Inject;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.ApiException;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.AuthRepository;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.ColorVisionManager;
import uk.ac.soton.comp2300.group42.user.PasswordValidation;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class RegisterViewModel {

    private static final Pattern HAS_LOWERCASE_PATTERN = Pattern.compile(".*[a-z].*");
    private static final Pattern HAS_UPPERCASE_PATTERN = Pattern.compile(".*[A-Z].*");
    private static final Pattern HAS_SPECIAL_PATTERN = Pattern.compile(".*[^A-Za-z0-9\\s].*");

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

        if (!isPasswordSecure(password.get())) {
            setResponse(buildPasswordInsecurityMessage(password.get()));
            return false;
        }

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

    private static boolean isPasswordSecure(String value) {
        return value.matches(PasswordValidation.PASSWORD_QUALITY_REGEX);
    }

    private static String buildPasswordInsecurityMessage(String value) {
        List<String> missingRequirements = new ArrayList<>();

        if (value.length() < 8)
            missingRequirements.add("at least 8 characters");

        if (!HAS_LOWERCASE_PATTERN.matcher(value).matches())
            missingRequirements.add("one lowercase letter");

        if (!HAS_UPPERCASE_PATTERN.matcher(value).matches())
            missingRequirements.add("one uppercase letter");

        if (!HAS_SPECIAL_PATTERN.matcher(value).matches())
            missingRequirements.add("one special character");

        if (missingRequirements.isEmpty())
            return PasswordValidation.PASSWORD_QUALITY_MESSAGE;

        return "Password is insecure: missing " + formatMissingRequirements(missingRequirements) + ".";
    }

    private static String formatMissingRequirements(List<String> missingRequirements) {
        if (missingRequirements.size() == 1)
            return missingRequirements.getFirst();

        if (missingRequirements.size() == 2)
            return missingRequirements.get(0) + " and " + missingRequirements.get(1);

        String allButLast = String.join(", ", missingRequirements.subList(0, missingRequirements.size() - 1));
        return allButLast + ", and " + missingRequirements.getLast();
    }

    public StringProperty nameProperty() { return name; }
    public StringProperty emailProperty() { return email; }
    public StringProperty passwordProperty() { return password; }
    public StringProperty confirmPasswordProperty() { return confirmPassword; }
    public StringProperty responseMessageProperty() { return responseMessage; }
    public ObjectProperty<ColorVisionManager.ColorRole> responseColorProperty() { return responseColor; }
}
