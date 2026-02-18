package uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel;

import uk.ac.soton.comp2300.group42.energyclient.ui.model.PreferencesModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.Repository;

import java.util.concurrent.CompletableFuture;

public class SharedDashboardViewModel {

    private final Repository repository;

    private final EnergyUsageWidgetViewModel widgetVM;

    public SharedDashboardViewModel(Repository repository, EnergyUsageWidgetViewModel widgetVM) {
        this.repository = repository;
        this.widgetVM = widgetVM;

        CompletableFuture.runAsync(repository::fetchAllData); // Run on a background thread so UI doesn't hang if the API is slow
    }

    public EnergyUsageWidgetViewModel getWidgetVM() { return widgetVM; }
    public Repository getRepository() { return repository; }
    public PreferencesModel getPreferences() { return repository.getPreferences(); }
}
