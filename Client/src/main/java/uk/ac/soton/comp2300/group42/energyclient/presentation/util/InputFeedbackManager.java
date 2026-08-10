package uk.ac.soton.comp2300.group42.energyclient.presentation.util;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.animation.PauseTransition;
import javafx.scene.control.TextInputControl;
import javafx.util.Duration;
import uk.ac.soton.comp2300.group42.energyclient.presentation.services.NotificationService;

import java.util.Objects;
import java.util.function.Function;

public class InputFeedbackManager {
    private final NotificationService notificationService;

    @Inject
    public InputFeedbackManager(NotificationService notificationService) {
        this.notificationService = Objects.requireNonNull(notificationService);
    }

    public void showPopup(String message) {
        notificationService.showPopup(message);
    }

    public void showPopup(String title, String description) {
        notificationService.showPopup(title, description);
    }

    // consider adding feedback when user presses enter

    public void bindOnFocusLost(TextInputControl field, Function<String, String> messageFactory) {
        Objects.requireNonNull(field);
        Objects.requireNonNull(messageFactory);

        field.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (wasFocused && !isFocused) {
                String value = safeTrim(field.getText());
                if (value.isEmpty()) return;

                String message = messageFactory.apply(value);
                notificationService.showPopup(message);
            }
        });
    }

    // consider adding feedback when user stops typing for a short period of time

    private static String safeTrim(String s) {
        return s == null ? "" : s.trim();
    }
}
