package uk.ac.soton.comp2300.group42.energyclient.presentation.controller;

import com.google.inject.Inject;
import javafx.fxml.FXML;

import uk.ac.soton.comp2300.group42.energyclient.presentation.util.Navigator;
import uk.ac.soton.comp2300.group42.energyclient.presentation.view.components.*;
import uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.EnergyUsageViewModel;
import uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.UpcomingActivationsViewModel;

public class SimpleDashboardController {

    @FXML private EnergyUsageWidget energyWidget;
    @FXML private UpcomingActivationsWidget activationsWidget;
    @FXML private ActivationEditModal activationEditModal;



    private final EnergyUsageViewModel energyWidgetVM;
    private final UpcomingActivationsViewModel activationsWidgetVM;

    @Inject public SimpleDashboardController(EnergyUsageViewModel energyWidgetVM,
                                             UpcomingActivationsViewModel activationsWidgetVM) {
        this.energyWidgetVM = energyWidgetVM;
        this.activationsWidgetVM = activationsWidgetVM;
    }

    @FXML private void initialize() {
        energyWidget.bindComponents(energyWidgetVM);
        energyWidgetVM.startAutoUpdateTest();
        activationsWidget.bindComponents(activationsWidgetVM, activationEditModal);

        activationsWidget.loadActivationsAsync();
    }

    @FXML private void onManageHouses() {
        Navigator.goTo("ManageHouses.fxml");
    }



    @FXML private void onProgressTracking() {
        Navigator.goTo("ProgressTracking.fxml");
    }
}
