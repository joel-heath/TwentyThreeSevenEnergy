package uk.ac.soton.comp2300.group42.energyclient.presentation.controller;

import com.google.inject.Inject;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import uk.ac.soton.comp2300.group42.common.Role;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.ColorVisionManager;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHouse;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableHousemate;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.Navigator;
import uk.ac.soton.comp2300.group42.energyclient.presentation.view.components.Modal;
import uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.ManageHousesViewModel;

import static uk.ac.soton.comp2300.group42.energyclient.presentation.util.ControllerUtils.createConverter;

public class ManageHousesController {

    private static final String HOUSEMATE_CARD_STYLE =
            "-fx-background-radius: 5; -fx-padding: 5; -fx-spacing: 5";

    @FXML private ComboBox<ObservableHouse> activeHouseComboBox;
    @FXML private VBox housematesContainer;
    @FXML private TextField inviteHousemateField;
    @FXML private TextField newHouseNameField;
    @FXML private TextField newHouseAddressField;
    @FXML private Button deleteHouseButton;
    @FXML private Button leaveHouseButton;
    @FXML private VBox inviteContainer;

    @FXML private Modal editHouseModal;
    @FXML private TextField editHouseNameField;
    @FXML private TextField editAddressField;

    private final ManageHousesViewModel vm;

    @Inject public ManageHousesController(ManageHousesViewModel vm) {
        this.vm = vm;
    }

    @FXML private void initialize() {
        activeHouseComboBox.setConverter(createConverter(ObservableHouse::getName));
        activeHouseComboBox.setItems(vm.getHouseList());
        activeHouseComboBox.valueProperty().bindBidirectional(vm.activeHouseProperty());

        deleteHouseButton.visibleProperty().bind(vm.isOwnerProperty());
        leaveHouseButton.visibleProperty().bind(vm.canLeaveHouseProperty());
        inviteContainer.visibleProperty().bind(vm.canInviteProperty());

        newHouseNameField.textProperty().bindBidirectional(vm.newHouseNameProperty());
        newHouseAddressField.textProperty().bindBidirectional(vm.newHouseAddressProperty());
        inviteHousemateField.textProperty().bindBidirectional(vm.inviteEmailProperty());

        editHouseNameField.textProperty().bindBidirectional(vm.editHouseNameProperty());
        editAddressField.textProperty().bindBidirectional(vm.editHouseAddressProperty());

        vm.hasNewHouseNameErrorProperty().subscribe(hasError ->
                newHouseNameField.setStyle(hasError ? "-fx-border-color: " + ColorVisionManager.getWebColor(ColorVisionManager.ColorRole.VALIDATION_ERROR) + ";" : "")
        );
        vm.hasNewHouseAddressErrorProperty().subscribe(hasError ->
                newHouseAddressField.setStyle(hasError ? "-fx-border-color: " + ColorVisionManager.getWebColor(ColorVisionManager.ColorRole.VALIDATION_ERROR) + ";" : "")
        );
        vm.hasInviteEmailErrorProperty().subscribe(hasError ->
                inviteHousemateField.setStyle(hasError ? "-fx-border-color: " + ColorVisionManager.getWebColor(ColorVisionManager.ColorRole.VALIDATION_ERROR) + ";" : "")
        );

        vm.isEditingHouseProperty().subscribe(isEditing -> {
            if (isEditing)
                editHouseModal.show();
            else
                editHouseModal.close();
        });

        bindHousemates();

        vm.loadData();
    }

    @FXML private void onCreateNewHouse() { vm.createHouse(); }
    @FXML private void onInviteHousemate() { vm.inviteHousemate(); }
    @FXML private void onEditHouse() { vm.openEditModal(); }
    @FXML private void onSaveHouseEdits() { vm.saveHouseEdits(); }
    @FXML private void onCloseEditModal() { vm.closeEditModal(); }
    @FXML private void onDeleteHouse() { vm.deleteActiveHouse(); }
    @FXML private void onLeaveHouse() { vm.leaveActiveHouse(); }
    @FXML private void onManageAppliances() { Navigator.goTo("ManageAppliances.fxml"); }

    private void bindHousemates() {
        ObservableList<ObservableHousemate> housemates = vm.getHousemates();
        renderHousemates(housemates);
        housemates.subscribe(() -> renderHousemates(housemates));
    }

    private void renderHousemates(ObservableList<ObservableHousemate> housemates) {
        housematesContainer.getChildren().setAll(
                housemates.stream().map(this::createHousemateView).toList()
        );
    }

    private Pane createHousemateView(ObservableHousemate housemate) {
        VBox card = new VBox();
        card.styleProperty().bind(ColorVisionManager.visionProperty().map(
                vision -> "-fx-background-color: " + ColorVisionManager.getWebColor(
                        vision, ColorVisionManager.ColorRole.CARD_SURFACE
                ) + "; " + HOUSEMATE_CARD_STYLE
        ));

        Label name = new Label();
        Label email = new Label();
        name.textProperty().bind(housemate.nameProperty());
        email.textProperty().bind(housemate.emailProperty());
        card.getChildren().addAll(name, email);

        if (vm.isOwnerProperty().get()) {
            Button kickButton = new Button("Kick");
            kickButton.setOnAction(_ -> vm.kickHousemate(housemate));

            ComboBox<Role> roleComboBox = new ComboBox<>();
            roleComboBox.getItems().setAll(Role.values());
            roleComboBox.valueProperty().bindBidirectional(housemate.roleProperty());
            roleComboBox.setConverter(createConverter(Role::getName));

            card.getChildren().addAll(roleComboBox, kickButton);
        }

        card.setUserData(housemate);
        return card;
    }
}
