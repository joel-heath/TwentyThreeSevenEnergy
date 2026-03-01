package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import com.google.inject.Inject;
import javafx.beans.property.DoubleProperty;
import uk.ac.soton.comp2300.group42.energyclient.domain.session.SessionManager;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;
import uk.ac.soton.comp2300.group42.energyclient.presentation.store.UserStore;

public class SettingsViewModel {

    private final UserStore userStore;
    private final SessionManager sessionManager;
    private final ObservablePreferences preferences;
    private final DoubleProperty costGoal;

    @Inject public SettingsViewModel(UserStore userStore, SessionManager sessionManager) {
        this.userStore = userStore;
        this.sessionManager = sessionManager;
        this.preferences = userStore.getPreferences();
        this.costGoal = preferences.energyGoalProperty();
    }

    public boolean isLoggedIn() {
        return sessionManager.isLoggedIn();
    }

    public ObservablePreferences getPreferences() {
        return preferences;
    }

    public void setCostGoal(double goal) {
        costGoal.set(goal);
    }

    public void save() {
        userStore.savePreferences();
    }
}