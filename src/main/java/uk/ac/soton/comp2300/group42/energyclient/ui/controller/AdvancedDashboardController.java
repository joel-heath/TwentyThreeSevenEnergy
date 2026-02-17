package uk.ac.soton.comp2300.group42.energyclient.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import jdk.javadoc.doclet.Reporter;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.Repository;
import uk.ac.soton.comp2300.group42.energyclient.ui.view.components.EnergyUsageWidget;
import uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.AdvancedDashboardViewModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.EnergyUsageWidgetViewModel;


public class AdvancedDashboardController {
    @FXML private VBox energyWidgetContainer;

    private final AdvancedDashboardViewModel vm;

    public AdvancedDashboardController(AdvancedDashboardViewModel vm) {
        this.vm = vm;
    }

    @FXML private void initialize() {
        EnergyUsageWidgetViewModel widgetVm =
                new EnergyUsageWidgetViewModel(vm.getRepository().getPreferences());
        EnergyUsageWidget widget = new EnergyUsageWidget(widgetVm);
        energyWidgetContainer.getChildren().add(widget);
    }
}
