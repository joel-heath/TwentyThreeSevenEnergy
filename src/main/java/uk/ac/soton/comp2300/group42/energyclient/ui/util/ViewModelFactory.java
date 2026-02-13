package uk.ac.soton.comp2300.group42.energyclient.ui.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import uk.ac.soton.comp2300.group42.energyclient.data.AuthenticatedHttpClient;
import uk.ac.soton.comp2300.group42.energyclient.data.api.UserClient;
import uk.ac.soton.comp2300.group42.energyclient.ui.controller.*;
import uk.ac.soton.comp2300.group42.energyclient.ui.controller.debug.DashboardDebugController;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.EnergyCalculator;
import uk.ac.soton.comp2300.group42.energyclient.data.api.ActivationClient;
import uk.ac.soton.comp2300.group42.energyclient.data.api.ApplianceClient;
import uk.ac.soton.comp2300.group42.energyclient.ui.services.NotificationService;
import uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.*;
import uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.debug.DashboardDebugViewModel;


// The factory exists to pass the same instance of any given model all around the UI,
// e.g. to allow Schedule page to add a reminder to the ActivationClient
//      and for the Dashboard page to see that same reminder.

public class ViewModelFactory {

    private final Repository repository;
    private final EnergyCalculator energyCalculator;

    public ViewModelFactory() {
        AuthenticatedHttpClient httpClient = new AuthenticatedHttpClient();
        ObjectMapper objectMapper = new ObjectMapper();
        ApplianceClient applianceClient = new ApplianceClient(httpClient, objectMapper);
        ActivationClient activationClient = new ActivationClient(httpClient, objectMapper);
        UserClient userClient = new UserClient(httpClient, objectMapper);

        ModelFactory modelFactory = new ModelFactory();
        NotificationService notificationService = new NotificationService();

        this.repository = new Repository(applianceClient, activationClient, userClient, notificationService, modelFactory);
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
            case Class<?> c when c == ManageHousesController.class
                    -> new ManageHousesViewModel(repository);
            case Class<?> c when c == SettingsController.class
                    -> new SettingsViewModel(repository);
            case Class<?> c when c == LoginController.class || c == SignupController.class
                    -> new LoginViewModel(repository);
            case Class<?> c when c == DashboardDebugController.class
                    -> new DashboardDebugViewModel(repository, energyCalculator);
            default -> null;
        };
    }
}