package uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel;

import uk.ac.soton.comp2300.group42.energyclient.ui.util.Repository;

public class AdvancedDashboardViewModel {
    private final Repository repository;

    public AdvancedDashboardViewModel(Repository repository) {
        this.repository = repository;
    }

    public Repository getRepository() { return repository; }
}
