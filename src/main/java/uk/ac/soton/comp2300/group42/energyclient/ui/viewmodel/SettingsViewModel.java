package uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel;

import javafx.beans.property.DoubleProperty;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.PreferencesModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.Repository;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.Navigator; // Add this import

public class SettingsViewModel {
    private final Repository repository;
    private final PreferencesModel preferences;
    private final DoubleProperty costGoal;

    public SettingsViewModel(Repository repository) {
        this.repository = repository;
        this.preferences = repository.getPreferences();
        this.costGoal = preferences.energyGoalProperty();
    }

    // ADD THIS METHOD
    public Navigator getNavigator() {
        return new Navigator();
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