package uk.ac.soton.comp2300.group42.energyclient.presentation.view.components;

import javafx.beans.binding.Bindings;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableActivation;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservableAppliance;
import uk.ac.soton.comp2300.group42.energyclient.presentation.util.Navigator;
import uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.ActivationEditViewModel;
import uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.UpcomingActivationsViewModel;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

import static uk.ac.soton.comp2300.group42.energyclient.presentation.util.ControllerUtils.formatDay;

public class UpcomingActivationsWidget extends VBox {

    @FXML private HBox scheduleContainer;

    private UpcomingActivationsViewModel vm;

    public void bindComponents(UpcomingActivationsViewModel vm,
                               ActivationEditViewModel editVm,
                               ActivationEditModal modal) {
        this.vm = vm;
        bindActivations();
        modal.bindComponents(editVm);
        vm.selectedActivationProperty().subscribe(activation -> {
            if (activation != null) {
                modal.show(activation);
                vm.selectActivation(null);
            }
        });
    }

    public CompletableFuture<Void> loadActivationsAsync() {
        return vm.refreshActivationsAsync();
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

    private Pane createActivationView(ObservableActivation activation) {
        VBox card = new VBox();
        card.getStyleClass().add("activation-card");

        Label nameLabel = new Label();
        Label timeLabel = new Label();
        Label dateLabel = new Label();

        nameLabel.textProperty().bind(activation.applianceProperty().flatMap(ObservableAppliance::nameProperty));
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
        card.setOnMouseClicked(_ -> vm.selectActivation(activation));
        card.setUserData(activation);

        return card;
    }

    private void bindActivations() {
        SortedList<ObservableActivation> activations = vm.getActivations();
        renderActivations(activations);
        activations.subscribe(() -> renderActivations(activations));
    }

    private void renderActivations(SortedList<ObservableActivation> activations) {
        scheduleContainer.getChildren().setAll(
                activations.stream().map(this::createActivationView).toList()
        );
    }
}
