package uk.ac.soton.comp2300.group42.energyclient.ui.controller;

import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import uk.ac.soton.comp2300.group42.energyclient.data.api.EnergyParser;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.EnergyPriceModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.services.EnergyPriceService;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.Navigator;
import uk.ac.soton.comp2300.group42.energyclient.ui.view.components.*;
import uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.EnergyUsageViewModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.UpcomingActivationsViewModel;

public class SimpleDashboardController {

    @FXML private EnergyUsageWidget energyWidget;
    @FXML private UpcomingActivationsWidget activationsWidget;
    @FXML private ActivationEditModal activationEditModal;

    @FXML private Label priceLabel;

    private final EnergyPriceService service = new EnergyPriceService();

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

        loadPrice();
    }

    @FXML private void onManageHouses() {
        Navigator.goTo("ManageHouses.fxml");
    }

    private void loadPrice() {
        new Thread(() -> {
            try {
                String json = service.fetchRawData();
                EnergyPriceModel price = EnergyParser.parse(json);

                Platform.runLater(() ->
                        priceLabel.setText(
                                String.format("%.2f p/kWh", price.getPrice())
                        )
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
