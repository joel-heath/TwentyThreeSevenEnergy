package uk.ac.soton.comp2300.group42.energyclient.ui.controller;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import uk.ac.soton.comp2300.group42.energyclient.ui.view.components.*;
import uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.EnergyUsageViewModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.UpcomingActivationsViewModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.Navigator;

public class AdvancedDashboardController {

    @FXML private EnergyUsageWidget energyWidget;
    @FXML private UpcomingActivationsWidget upcomingActivationsWidget;
    @FXML private ActivationEditModal activationEditModal;

    private final EnergyUsageViewModel energyWidgetVM;
    private final UpcomingActivationsViewModel activationsWidgetVM;

    @Inject public AdvancedDashboardController(EnergyUsageViewModel energyWidgetVM,
                                               UpcomingActivationsViewModel activationsWidgetVM) {
        this.energyWidgetVM = energyWidgetVM;
        this.activationsWidgetVM = activationsWidgetVM;
    }

    @FXML private void initialize() {
        energyWidget.bindComponents(energyWidgetVM);
        energyWidgetVM.startAutoUpdateTest();

        upcomingActivationsWidget.bindComponents(activationsWidgetVM, activationEditModal);
    }

    @FXML private void onManageHouses() {
        Navigator.goTo("ManageHouses.fxml");
    }

    @FXML private void onProgressTracking() {
        // Navigator.goTo("ProgressTracking.fxml");
    }
}
