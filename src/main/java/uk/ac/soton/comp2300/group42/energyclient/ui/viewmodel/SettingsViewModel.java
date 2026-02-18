package uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel;

import com.google.inject.Inject;
import javafx.beans.property.DoubleProperty;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.PreferencesModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.Repository;

public class SettingsViewModel {

    private final Repository repository;
    private final PreferencesModel preferences;
    private final DoubleProperty costGoal;

    @Inject public SettingsViewModel(Repository repository) {
        this.repository = repository;
        this.preferences = repository.getPreferences();
        this.costGoal = preferences.energyGoalProperty();
    }

    public PreferencesModel getPreferences() {
        return preferences;
    }

    public void setCostGoal(double goal) {
        costGoal.set(goal);
    }

    public void save() {
        repository.savePreferences();
    }
}