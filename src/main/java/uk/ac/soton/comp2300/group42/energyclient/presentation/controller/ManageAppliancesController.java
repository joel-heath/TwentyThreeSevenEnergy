package uk.ac.soton.comp2300.group42.energyclient.presentation.controller;

import com.google.inject.Inject;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableAppliance;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.StyleClassUtils;
import uk.ac.soton.comp2300.group42.energyclient.presentation.view.components.Modal;
import uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.ManageAppliancesViewModel;

public class ManageAppliancesController {

    @FXML private Label houseLabel;
    @FXML private VBox appliancesContainer;
    @FXML private TextField addApplianceField;
    @FXML private Button deleteApplianceButton;
    @FXML private VBox addContainer;

    @FXML private Modal editApplianceModal;
    @FXML private TextField editApplianceNameField;

    private final ManageAppliancesViewModel vm;

    @Inject public ManageAppliancesController(ManageAppliancesViewModel vm) {
        this.vm = vm;
    }

    @FXML private void initialize() {
        houseLabel.textProperty().bind(vm.activeHouseNameProperty());
        deleteApplianceButton.visibleProperty().bind(vm.hasReadWritePermissionProperty());
        addContainer.visibleProperty().bind(vm.hasReadWritePermissionProperty());

        addApplianceField.textProperty().bindBidirectional(vm.newApplianceNameProperty());
        editApplianceNameField.textProperty().bindBidirectional(vm.editApplianceNameProperty());

        StyleClassUtils.bindBooleanClass(addApplianceField, vm.hasNewApplianceErrorProperty(), "validation-error");

        vm.selectedApplianceProperty().subscribe(selected -> {
            if (selected != null)
                editApplianceModal.show();
            else
                editApplianceModal.close();
        });

        bindActivations();
        vm.loadData();
    }
    @FXML private void onAddAppliance() {
        vm.addAppliance();
    }

    @FXML private void onSaveApplianceEdits() {
        vm.saveApplianceEdits();
    }

    @FXML private void onDeleteAppliance() {
        vm.deleteSelectedAppliance();
    }

    @FXML private void onCloseEditModal() {
        vm.selectApplianceForEdit(null);
    }

    private Pane createApplianceView(ObservableAppliance appliance) {
        VBox card = new VBox();
        card.getStyleClass().add("list-card");

        Label name = new Label();
        name.textProperty().bind(appliance.nameProperty());
        card.getChildren().add(name);

        if (vm.hasReadWritePermissionProperty().getValue()) {
            card.setOnMouseClicked(_ -> vm.selectApplianceForEdit(appliance));
        }

        card.setUserData(appliance);
        return card;
    }

    private void bindActivations() {
        ObservableList<ObservableAppliance> appliances = vm.getAppliances();
        renderActivations(appliances);
        appliances.subscribe(() -> renderActivations(appliances));
    }

    private void renderActivations(ObservableList<ObservableAppliance> appliances) {
        appliancesContainer.getChildren().setAll(
                appliances.stream()
                        .map(this::createApplianceView)
                        .toList()
        );
    }
}
