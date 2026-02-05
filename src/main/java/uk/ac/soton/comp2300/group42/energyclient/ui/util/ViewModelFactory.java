package uk.ac.soton.comp2300.group42.energyclient.ui.util;

import uk.ac.soton.comp2300.group42.energyclient.ui.controller.DashboardController;
import uk.ac.soton.comp2300.group42.energyclient.ui.controller.ScheduleController;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.EnergyCalculator;
import uk.ac.soton.comp2300.group42.energyclient.data.api.ActivationClient;
import uk.ac.soton.comp2300.group42.energyclient.data.api.ApplianceClient;
import uk.ac.soton.comp2300.group42.energyclient.ui.services.NotificationService;
import uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.DashboardViewModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.ScheduleViewModel;


// The factory exists to pass the same instance of any given model all around the UI,
// e.g. to allow Schedule page to add a reminder to the ActivationClient
//      and for the Dashboard page to see that same reminder.
// We can't just make ActivationClient a singleton because then we can't mock it for unit tests.

public class ViewModelFactory {
    private final ModelFactory modelFactory;
    private final ApplianceClient applianceClient;
    private final ActivationClient activationClient;
    private final EnergyCalculator energyCalculator;
    private final NotificationService notificationService;

    public ViewModelFactory() {
        this.modelFactory = new ModelFactory();
        this.applianceClient = new ApplianceClient();
        this.activationClient = new ActivationClient();
        this.energyCalculator = new EnergyCalculator();
        this.notificationService = new NotificationService(activationClient);
    }

    // You only need to add one of these clauses if the ViewModel you're adding requires any model parameters
    // Navigator handles calling parameterless constructors for ViewModels.

    public Object getViewModel(Class<?> controllerClass) {
        return switch (controllerClass) {
            case Class<?> c when c == DashboardController.class
                    -> new DashboardViewModel(modelFactory, energyCalculator, activationClient, applianceClient, notificationService);
            case Class<?> c when c == ScheduleController.class
                    -> new ScheduleViewModel(modelFactory, applianceClient, activationClient, notificationService);
            default -> null;
        };
    }
}