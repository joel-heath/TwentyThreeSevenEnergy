package uk.ac.soton.comp2300.group42.energyclient.util;

import uk.ac.soton.comp2300.group42.energyclient.controller.DashboardController;
import uk.ac.soton.comp2300.group42.energyclient.controller.ScheduleController;
import uk.ac.soton.comp2300.group42.energyclient.model.EnergyCalculator;
import uk.ac.soton.comp2300.group42.energyclient.model.repository.ActivationRepository;
import uk.ac.soton.comp2300.group42.energyclient.model.repository.ApplianceRepository;
import uk.ac.soton.comp2300.group42.energyclient.services.NotificationService;
import uk.ac.soton.comp2300.group42.energyclient.viewmodel.DashboardViewModel;
import uk.ac.soton.comp2300.group42.energyclient.viewmodel.ScheduleViewModel;


// The factory exists to pass the same instance of any given model all around the UI,
// e.g. to allow Schedule page to add a reminder to the ActivationRepository
//      and for the Dashboard page to see that same reminder.
// We can't just make ActivationRepository a singleton because then we can't mock it for unit tests.

public class ViewModelFactory {
    private final ApplianceRepository applianceRepository;
    private final ActivationRepository activationRepository;
    private final EnergyCalculator energyCalculator;
    private final NotificationService notificationService;

    public ViewModelFactory() {
        this.applianceRepository = new ApplianceRepository();
        this.activationRepository = new ActivationRepository();
        this.energyCalculator = new EnergyCalculator();
        this.notificationService = new NotificationService(activationRepository);
    }

    // You only need to add one of these clauses if the ViewModel you're adding requires any model parameters
    // Navigator handles calling parameterless constructors for ViewModels.

    public Object getViewModel(Class<?> controllerClass) {
        return switch (controllerClass) {
            case Class<?> c when c == DashboardController.class
                    -> new DashboardViewModel(energyCalculator, activationRepository, applianceRepository, notificationService);
            case Class<?> c when c == ScheduleController.class
                    -> new ScheduleViewModel(applianceRepository, activationRepository, notificationService);
            default -> null;
        };
    }
}