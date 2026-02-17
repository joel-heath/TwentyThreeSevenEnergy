package uk.ac.soton.comp2300.group42.energyclient.ui.controller;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import uk.ac.soton.comp2300.group42.energyclient.data.api.Role;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.HouseModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.HousemateModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.view.components.Modal;
import uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.ManageHousesViewModel;

import static uk.ac.soton.comp2300.group42.energyclient.ui.util.ControllerUtils.createConverter;

public class ManageHousesController {
    @FXML private ComboBox<HouseModel> activeHouseComboBox;
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
    @FXML private Label responseLabel;

    private final ManageHousesViewModel vm;
    public ManageHousesController(ManageHousesViewModel vm) { this.vm = vm; }

    @FXML private void initialize() {
        activeHouseComboBox.getItems().setAll(vm.getHouseList());
        activeHouseComboBox.setConverter(createConverter(HouseModel::getName));
        activeHouseComboBox.valueProperty().bindBidirectional(vm.activeHouseProperty());
        activeHouseComboBox.valueProperty()
                .map(_ -> vm.getHousemates()
                            .stream()
                            .map(this::createHousemateView)
                            .toList())
                .subscribe(housemates -> housematesContainer.getChildren().setAll(housemates));

        deleteHouseButton.visibleProperty().bind(vm.currentRoleProperty().isEqualTo(Role.OWNER));
        leaveHouseButton.visibleProperty().bind(vm.currentRoleProperty().map(_ -> vm.canLeaveHouse()));
        inviteContainer.visibleProperty().bind(vm.currentRoleProperty().isNotEqualTo(Role.GUEST));
    }

    @FXML private void onEditHouse() {
        editHouseModal.show();
        editHouseNameField.setText(vm.getActiveHouse().getName());
        editAddressField.setText(vm.getActiveHouse().getAddress());
    }

    @FXML private void onSaveHouseEdits() {
        vm.editActiveHouse(editHouseNameField.getText(), editAddressField.getText());
        activeHouseComboBox.getItems().setAll(vm.getHouseList());
        editHouseModal.close();
    }

    @FXML private void onDeleteHouse() {
        // worth a confirmation popup
        vm.deleteActiveHouse();
        editHouseModal.close();
    }

    @FXML private void onLeaveHouse() {
        // worth a confirmation popup
        vm.leaveActiveHouse();
        editHouseModal.close();
    }

    @FXML private void onCloseEditModal() {
        editHouseModal.close();
    }

    @FXML private void onCreateNewHouse() {
        vm.createHouse(newHouseNameField.getText(), newHouseAddressField.getText());
        activeHouseComboBox.getItems().setAll(vm.getHouseList());
    }

    @FXML private void onInviteHousemate() {
        vm.inviteHousemate(inviteHousemateField.getText());
    }

    private Pane createHousemateView(HousemateModel housemate) {
        VBox card = new VBox();
        // card.setPrefSize(100, 150);
        card.setStyle("-fx-background-color: lightblue; -fx-background-radius: 5; -fx-padding: 5; -fx-spacing: 5");

        Label name = new Label();
        Label email = new Label();
        name.textProperty().bind(Bindings.concat(housemate.getForename()).concat(" ").concat(housemate.getSurname()));
        email.textProperty().bind(housemate.emailProperty());
        card.getChildren().addAll(name, email);

        if (vm.getCurrentUserRole() == Role.OWNER) {
            Button kickButton = new Button("Kick");
            kickButton.setOnAction(_ -> {
                vm.kickHousemate(housemate);
                housematesContainer.getChildren()
                        .removeIf(node -> node.getUserData() == housemate);
            });
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
