package uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel;

import uk.ac.soton.comp2300.group42.energyclient.ui.model.PreferencesModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.Repository;

public class DashboardViewModel {
    private final Repository repository;

    public DashboardViewModel(Repository repository) {
        this.repository = repository;
    }

    public PreferencesModel getPreferences() { return repository.getPreferences(); }
}
