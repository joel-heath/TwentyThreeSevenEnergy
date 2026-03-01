package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import com.google.inject.Inject;
import javafx.beans.property.DoubleProperty;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.AuthRepository;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;
import uk.ac.soton.comp2300.group42.energyclient.presentation.store.UserStore;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.Navigator;

public class SettingsViewModel {

    private final UserStore userStore;
    private final AuthRepository authRepo;
    private final ObservablePreferences preferences;
    private final DoubleProperty costGoal;

    @Inject public SettingsViewModel(UserStore userStore, AuthRepository authRepo) {
        this.userStore = userStore;
        this.authRepo = authRepo;
        this.preferences = userStore.getPreferences();
        this.costGoal = preferences.energyGoalProperty();
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

    public void logout() {
        authRepo.logout();
    }

    public void login() {
        Navigator.goTo("Login.fxml");
    }
}