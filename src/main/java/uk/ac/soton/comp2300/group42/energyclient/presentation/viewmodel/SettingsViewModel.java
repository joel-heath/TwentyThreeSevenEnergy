package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import com.google.inject.Inject;
import javafx.beans.property.DoubleProperty;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.AuthRepository;
import uk.ac.soton.comp2300.group42.energyclient.presentation.model.PreferencesModel;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.IDoEverything;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.Navigator;

public class SettingsViewModel {

    private final IDoEverything IDoEverything;
    private final AuthRepository authRepo;
    private final PreferencesModel preferences;
    private final DoubleProperty costGoal;

    @Inject public SettingsViewModel(IDoEverything IDoEverything, AuthRepository authRepo) {
        this.IDoEverything = IDoEverything;
        this.authRepo = authRepo;
        this.preferences = IDoEverything.getPreferences();
        this.costGoal = preferences.energyGoalProperty();
    }

    public PreferencesModel getPreferences() {
        return preferences;
    }

    public void setCostGoal(double goal) {
        costGoal.set(goal);
    }

    public void save() {
        IDoEverything.savePreferences();
    }

    public void logout() {
        authRepo.logout();
    }

    public void login() {
        Navigator.goTo("Login.fxml");
    }
}