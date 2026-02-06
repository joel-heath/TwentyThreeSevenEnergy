package uk.ac.soton.comp2300.group42.energyclient.ui.util;

import uk.ac.soton.comp2300.group42.energyclient.ui.controller.*;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.EnergyCalculator;
import uk.ac.soton.comp2300.group42.energyclient.data.api.ActivationClient;
import uk.ac.soton.comp2300.group42.energyclient.data.api.ApplianceClient;
import uk.ac.soton.comp2300.group42.energyclient.ui.services.NotificationService;
import uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.DashboardViewModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.SimpleDashboardViewModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.ScheduleViewModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.SettingsViewModel;


// The factory exists to pass the same instance of any given model all around the UI,
// e.g. to allow Schedule page to add a reminder to the ActivationClient
//      and for the Dashboard page to see that same reminder.
// We can't just make ActivationClient a singleton because then we can't mock it for unit tests.

public class ViewModelFactory {
    private final ApplianceClient applianceClient;
    private final ActivationClient activationClient;
    private final ModelFactory modelFactory;
    private final Repository repository;
    private final NotificationService notificationService;
    private final EnergyCalculator energyCalculator;

    public ViewModelFactory() {
        this.applianceClient = new ApplianceClient();
        this.activationClient = new ActivationClient();
        this.modelFactory = new ModelFactory();
        this.notificationService = new NotificationService();
        this.repository = new Repository(applianceClient, activationClient, notificationService, modelFactory);
        this.energyCalculator = new EnergyCalculator();
    }

    // You only need to add one of these clauses if the ViewModel you're adding requires any model parameters
    // Navigator handles calling parameterless constructors for ViewModels.

    // for access by the Navigator
    public Repository getRepository() { return repository; }

    public Object getViewModel(Class<?> controllerClass) {
        return switch (controllerClass) {
            case Class<?> c when c == DashboardController.class
                    -> new DashboardViewModel(repository);
            case Class<?> c when c == SimpleDashboardController.class
                    -> new SimpleDashboardViewModel(repository, energyCalculator);
            case Class<?> c when c == ScheduleController.class
                    -> new ScheduleViewModel(repository);
            case Class<?> c when c == SettingsController.class
                    -> new SettingsViewModel(repository);
            default -> null;
        };
    }
}