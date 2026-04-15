package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import com.google.inject.Inject;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import uk.ac.soton.comp2300.group42.energyclient.domain.session.SessionManager;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.InputFeedbackManager;
import uk.ac.soton.comp2300.group42.preferences.Mode;
import uk.ac.soton.comp2300.group42.preferences.Theme;

public class SettingsViewModel {

    private final SessionManager sessionManager;
    private final ObservablePreferences preferences;
    private final InputFeedbackManager inputFeedbackManager;

    private final ObservableList<Theme> availableThemes;
    private final ObservableList<Mode> availableModes;

    private final StringProperty costGoalInput = new SimpleStringProperty("");
    private final BooleanProperty hasCostGoalError = new SimpleBooleanProperty(false);

    @Inject public SettingsViewModel(ObservablePreferences preferences, SessionManager sessionManager, InputFeedbackManager feedbackManager) {
        this.sessionManager = sessionManager;
        this.preferences = preferences;
        this.inputFeedbackManager = feedbackManager;

        this.availableThemes = FXCollections.observableArrayList(Theme.values());
        this.availableModes = FXCollections.observableArrayList(Mode.values());
    }

    public void updateCostGoal() {
        String raw = costGoalInput.get() == null ? "" : costGoalInput.get().trim();

        if (raw.isEmpty()) {
            hasCostGoalError.set(true);
            inputFeedbackManager.showPopup("Cost goal not updated", "Please enter a cost goal before clicking Set Goal.");
            return;
        }

        try {
            String text = raw.replace("£", "").trim();
            double value = Double.parseDouble(text);
            if (value <= 0) throw new NumberFormatException();

            preferences.energyGoalProperty().set(value);
            hasCostGoalError.set(false);
            costGoalInput.set("");

            inputFeedbackManager.showPopup("Cost goal updated", String.format("Your new cost goal is £%.2f.", value));

        } catch (NumberFormatException e) {
            hasCostGoalError.set(true);
            inputFeedbackManager.showPopup("Cost goal not updated", "Please enter a valid number greater than 0.");
        }
    }

    public boolean isLoggedIn() {
        return sessionManager.isLoggedIn();
    }

    public ObservableList<Theme> getAvailableThemes() { return availableThemes; }
    public ObservableList<Mode> getAvailableModes() { return availableModes; }

    public BooleanProperty shareLocationProperty() { return preferences.shareLocationProperty(); }
    public ObjectProperty<Theme> themeProperty() { return preferences.themeProperty(); }
    public ObjectProperty<Mode> modeProperty() { return preferences.modeProperty(); }

    public StringProperty costGoalInputProperty() { return costGoalInput; }
    public BooleanProperty hasCostGoalErrorProperty() { return hasCostGoalError; }
}