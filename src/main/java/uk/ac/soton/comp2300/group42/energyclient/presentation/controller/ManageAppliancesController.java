package uk.ac.soton.comp2300.group42.energyclient.presentation.controller;

import com.google.inject.Inject;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableAppliance;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.InputFeedbackManager;
import uk.ac.soton.comp2300.group42.energyclient.presentation.view.components.Modal;
import uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.ManageAppliancesViewModel;

import static uk.ac.soton.comp2300.group42.energyclient.presentation.util.ControllerUtils.setValidationError;

public class ManageAppliancesController {

    @FXML private Label houseLabel;
    @FXML private VBox appliancesContainer;
    @FXML private TextField addApplianceField;
    @FXML private Button deleteApplianceButton;
    @FXML private VBox addContainer;

    @FXML private Modal editApplianceModal;
    @FXML private TextField editApplianceNameField;

    private final ManageAppliancesViewModel vm;
    private final InputFeedbackManager inputFeedbackManager;

    private ObservableAppliance currentEditingAppliance;

    @Inject
    public ManageAppliancesController(ManageAppliancesViewModel vm, InputFeedbackManager inputFeedbackManager) {
        this.vm = vm;
        this.inputFeedbackManager = inputFeedbackManager;
    }

    @FXML private void initialize() {
        houseLabel.setText(vm.getActiveHouseName());
        deleteApplianceButton.visibleProperty().bind(vm.currentRoleProperty().map(_ -> vm.hasReadWritePermission()));
        addContainer.visibleProperty().bind(deleteApplianceButton.visibleProperty());
        bindActivations();

        vm.refreshAppliances().exceptionally(ex -> {
            inputFeedbackManager.showPopup("Error loading appliances", "An error occurred while loading appliances: " + ex.getMessage());
            return null;
        });
    }

    @FXML private void onSaveApplianceEdits() {
        vm.updateAppliance(currentEditingAppliance, editApplianceNameField.getText());
        editApplianceModal.close();
    }

    @FXML private void onDeleteAppliance() {
        vm.deleteAppliance(currentEditingAppliance);
        editApplianceModal.close();
    }

    @FXML private void onCloseEditModal() {
        currentEditingAppliance = null;
        editApplianceModal.close();
    }

    @FXML private void onAddAppliance() {
        String name = addApplianceField.getText() == null ? "" : addApplianceField.getText().trim();

        if (name.isBlank()) {
            inputFeedbackManager.showPopup(
                    "Appliance not added",
                    "Please enter an appliance name."
            );
            setValidationError(addApplianceField, true);
            return;
        }

        vm.createAppliance(name);
        inputFeedbackManager.showPopup("Appliance added", "\"" + name + "\" has been added.");
        setValidationError(addApplianceField, false);
        addApplianceField.clear();
    }

    private void showModal(ObservableAppliance appliance) {
        currentEditingAppliance = appliance;
        editApplianceNameField.setText(appliance.getName());
        editApplianceModal.show();
    }

    private Pane createApplianceView(ObservableAppliance appliance) {
        VBox card = new VBox();
        card.getStyleClass().add("list-card");

        Label name = new Label();
        name.textProperty().bind(appliance.nameProperty());
        card.getChildren().add(name);

        if (vm.hasReadWritePermission())
            card.setOnMouseClicked(_ -> showModal(appliance));

        card.setUserData(appliance);
        return card;
    }

    private void bindActivations() {
        ObservableList<ObservableAppliance> appliances = vm.getAppliances();
        renderActivations(appliances);
        appliances.addListener((ListChangeListener<ObservableAppliance>) _ -> renderActivations(appliances));
    }

    private void renderActivations(ObservableList<ObservableAppliance> appliances) {
        appliancesContainer.getChildren().setAll(
                appliances.stream()
                        .map(this::createApplianceView)
                        .toList()
        );
    }
}
