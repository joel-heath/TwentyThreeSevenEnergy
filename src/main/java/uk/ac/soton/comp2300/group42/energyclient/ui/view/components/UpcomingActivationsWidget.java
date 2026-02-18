package uk.ac.soton.comp2300.group42.energyclient.ui.view.components;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.ActivationModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.ApplianceModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.Navigator;
import uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.UpcomingActivationsViewModel;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

import static uk.ac.soton.comp2300.group42.energyclient.ui.util.ControllerUtils.formatDay;

public class UpcomingActivationsWidget extends VBox {

    @FXML private HBox scheduleContainer;

    private UpcomingActivationsViewModel vm;
    private ActivationEditModal editModal;

    public void bindComponents(UpcomingActivationsViewModel vm,
                               ActivationEditModal editModal) {
        this.vm = vm;
        this.editModal = editModal;

        editModal.bindComponents(vm);
        bindActivations();
    }

    public UpcomingActivationsWidget() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ScheduleApplianceWidget.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
    }

    @FXML private void onSchedule() {
        Navigator.goTo("Schedule.fxml");
    }

    private Pane createActivationView(ActivationModel activation) {
        VBox card = new VBox();
        card.setStyle("-fx-background-color: lightblue; -fx-background-radius: 5; -fx-padding: 5; -fx-spacing: 5");

        Label nameLabel = new Label();
        Label timeLabel = new Label();
        Label dateLabel = new Label();

        nameLabel.textProperty().bind(
                activation.applianceProperty().flatMap(ApplianceModel::nameProperty)
        );
        timeLabel.textProperty().bind(Bindings.createStringBinding(
                () -> activation.getActivationTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                activation.activationDateProperty()
        ));
        dateLabel.textProperty().bind(Bindings.createStringBinding(
                () -> formatDay(activation.getNextActivationDateTime()),
                activation.activationTimeProperty(),
                activation.activationDateProperty(),
                activation.recursMondayProperty(),
                activation.recursTuesdayProperty(),
                activation.recursWednesdayProperty(),
                activation.recursThursdayProperty(),
                activation.recursFridayProperty(),
                activation.recursSaturdayProperty(),
                activation.recursSundayProperty()
        ));
        card.getChildren().addAll(nameLabel, timeLabel, dateLabel);
        card.setOnMouseClicked(_ -> editModal.show(activation));
        card.setUserData(activation);

        return card;
    }

    private void bindActivations() {
        SortedList<ActivationModel> activations = vm.getActivations();

        scheduleContainer.getChildren().clear();
        for (ActivationModel activation : activations) {
            scheduleContainer.getChildren().add(createActivationView(activation));
        }

        activations.addListener((ListChangeListener<ActivationModel>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (int i = 0; i < change.getAddedSize(); i++) {
                        ActivationModel addedItem = change.getAddedSubList().get(i);
                        Node view = createActivationView(addedItem);
                        scheduleContainer.getChildren().add(change.getFrom() + i, view);
                    }
                }

                if (change.wasRemoved()) {
                    scheduleContainer.getChildren().remove(
                            change.getFrom(),
                            change.getFrom() + change.getRemovedSize()
                    );
                }

                if (change.wasPermutated() || change.wasUpdated()) {
                    FXCollections.sort(scheduleContainer.getChildren(),
                            Comparator.comparingInt(node -> activations.indexOf((ActivationModel) node.getUserData())));
                }
            }
        });
    }
}
