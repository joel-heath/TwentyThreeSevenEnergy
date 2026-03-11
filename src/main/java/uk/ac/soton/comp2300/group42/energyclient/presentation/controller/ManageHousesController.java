package uk.ac.soton.comp2300.group42.energyclient.presentation.controller;

import com.google.inject.Inject;
import javafx.application.Platform;
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
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.InputFeedbackManager;
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
    @FXML private Label responseLabel;

    private final ManageHousesViewModel vm;
    private final InputFeedbackManager inputFeedbackManager;

    @Inject public ManageHousesController(ManageHousesViewModel vm, InputFeedbackManager inputFeedbackManager) {
        this.vm = vm;
        this.inputFeedbackManager = inputFeedbackManager;
    }

    @FXML private void initialize() {
        activeHouseComboBox.setConverter(createConverter(ObservableHouse::getName));
        activeHouseComboBox.setItems(vm.getHouseList());
        activeHouseComboBox.valueProperty().bindBidirectional(vm.activeHouseProperty());
        activeHouseComboBox.valueProperty()
                .subscribe(_ -> {
                    vm.updateHousemates();
                    housematesContainer.getChildren().setAll(
                            vm.getHousemates()
                            .stream()
                            .map(this::createHousemateView)
                            .toList());
                });

        deleteHouseButton.visibleProperty().bind(vm.currentRoleProperty().isEqualTo(Role.OWNER));
        leaveHouseButton.visibleProperty().bind(vm.currentRoleProperty().map(_ -> vm.canLeaveHouse()));
        inviteContainer.visibleProperty().bind(vm.currentRoleProperty().isNotEqualTo(Role.GUEST));

        vm.refreshDataAsync().thenRunAsync(() -> {
            vm.updateHousemates();
            housematesContainer.getChildren().setAll(
            vm.getHousemates()
                    .stream()
                    .map(this::createHousemateView)
                    .toList());
        }, Platform::runLater).exceptionally(e -> {
            System.out.println("Error loading user data: " + e.getMessage());
            return null;
        });
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
        // TODO: worth a confirmation popup
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
        String name = newHouseNameField.getText() == null ? "" : newHouseNameField.getText().trim();
        String address = newHouseAddressField.getText() == null ? "" : newHouseAddressField.getText().trim();

        boolean hasError = false;

        if (name.isEmpty()) {
            newHouseAddressField.setStyle(
                    "-fx-border-color: " + ColorVisionManager.getWebColor(ColorVisionManager.ColorRole.VALIDATION_ERROR) + ";"
            );
            hasError = true;
        } else {
            newHouseAddressField.setStyle("");
        }

        if (address.isEmpty()) {
            newHouseAddressField.setStyle(
                    "-fx-border-color: " + ColorVisionManager.getWebColor(ColorVisionManager.ColorRole.VALIDATION_ERROR) + ";"
            );
            hasError = true;
        } else {
            newHouseAddressField.setStyle("");
        }

        if (hasError) {
            inputFeedbackManager.showPopup(
                    "House not created",
                    "Please enter a house name and address before creating a new house."
            );
            return;
        }
        vm.createHouse(name, address);
        activeHouseComboBox.getItems().setAll(vm.getHouseList());

        inputFeedbackManager.showPopup(
                "House created",
                "Created \"" + name + "\"."
        );

        newHouseNameField.clear();
        newHouseAddressField.clear();
    }

    @FXML private void onInviteHousemate() {
        String email = inviteHousemateField.getText() == null ? "" : inviteHousemateField.getText().trim();

        if (email.isEmpty()) {
            inputFeedbackManager.showPopup(
                    "Invite not sent",
                    "Please enter an email address before sending an invite."
            );
            inviteHousemateField.setStyle(
                    "-fx-border-color: " + ColorVisionManager.getWebColor(ColorVisionManager.ColorRole.VALIDATION_ERROR) + ";"
            );
            return;
        }

        vm.inviteHousemate(email);

        inputFeedbackManager.showPopup("Invite sent", "An invitation has been sent to " + email + ".");
        inviteHousemateField.setStyle("");
    }

    @FXML private void onManageAppliances() {
        Navigator.goTo("ManageAppliances.fxml");
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
