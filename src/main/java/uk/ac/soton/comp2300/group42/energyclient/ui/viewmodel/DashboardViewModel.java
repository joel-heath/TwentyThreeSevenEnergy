package uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.util.Duration;
import uk.ac.soton.comp2300.group42.energyclient.data.api.ActivationClient;
import uk.ac.soton.comp2300.group42.energyclient.data.api.ApplianceClient;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.ActivationModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.ApplianceModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.EnergyCalculator;
import uk.ac.soton.comp2300.group42.energyclient.data.dto.ActivationDTO;
import uk.ac.soton.comp2300.group42.energyclient.data.dto.ApplianceDTO;
import uk.ac.soton.comp2300.group42.energyclient.ui.services.NotificationService;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.ModelFactory;

import java.time.LocalDateTime;
import java.util.Comparator;

public class DashboardViewModel {

    private final IntegerProperty counter = new SimpleIntegerProperty(0);
    private final StringProperty costMessage = new SimpleStringProperty("Total Spent: £0.00");
    private final DoubleProperty cost = new SimpleDoubleProperty(0);
    private final StringProperty goalMessage = new SimpleStringProperty("Cost Goal: £1.00");
    private final DoubleProperty costGoal = new SimpleDoubleProperty(1);
    private final DoubleProperty usage = new SimpleDoubleProperty(0);

    private final ModelFactory modelFactory;
    private final ApplianceClient applianceClient;
    private final ActivationClient activationClient;
    private final NotificationService notificationService;
    private final ObservableList<ApplianceModel> appliances;
    private final SortedList<ActivationModel> activations;

    private final EnergyCalculator calc;

    public DashboardViewModel(ModelFactory modelFactory, EnergyCalculator calc, ActivationClient activationClient, ApplianceClient applianceClient, NotificationService notificationService) {
        this.modelFactory = modelFactory;
        this.calc = calc;
        this.activationClient = activationClient;
        this.applianceClient = applianceClient;
        this.notificationService = notificationService;

        appliances = loadAppliances();
        activations = new SortedList<>(loadActivations());
        activations.setComparator(Comparator.comparing(ActivationModel::getActivationTime));
    }

    private ObservableList<ApplianceModel> loadAppliances() {
        var dtos = applianceClient.findAll();
        var models = dtos.stream().map(modelFactory::getApplianceModel).toList();
        return FXCollections.observableArrayList(models);
    }

    private ObservableList<ActivationModel> loadActivations() {
        ObservableList<ActivationModel> activationsSource = FXCollections.observableArrayList(
                activation -> new Observable[] {
                        activation.activationTimeProperty(),
                        activation.applianceProperty()
                }
        );
        activationsSource.addAll(
                activationClient.findAll()
                        .stream()
                        .map(modelFactory::createActivationModel)
                        .toList()
        );
        return activationsSource;
    }

    public ObservableList<ApplianceModel> getAppliances() { return appliances; }
    public SortedList<ActivationModel> getActivations() { return activations; }
    public IntegerProperty counterProperty() { return counter; }
    public DoubleProperty usageProperty() { return usage; }
    public StringProperty costMessageProperty() { return costMessage; }
    public StringProperty goalMessageProperty() { return goalMessage; }

    public void incrementCounter() {
        counter.set(counter.get() + 1);
    }

    public void recalculateCost() {
        int joules = 1 + 5 * counter.get();
        double pounds = calc.convertJoulesToPounds(joules);
        cost.set(pounds);
        costMessage.set(String.format("Total Spent: £%.2f", pounds));
        recalculateUsage();
    }

    public void setCostGoal(double goal) {
        costGoal.set(goal);
        goalMessage.set(String.format("Cost Goal: £%.2f", goal));
        recalculateUsage();
    }

    public void recalculateUsage() {
        double cost = this.cost.get();
        double target = costGoal.get();
        double percentUsage = cost / target;
        usage.set(Math.max(0, Math.min(percentUsage, 1)));
    }

    public void startAutoUpdateTest() {
        Timeline testTimeline = new Timeline(
            new KeyFrame(Duration.seconds(1), _ -> {
                counter.set(counter.get() + 1);
                recalculateCost();
            })
        );
        testTimeline.setCycleCount(Timeline.INDEFINITE);
        testTimeline.play();
    }

    public void removeActivation(ActivationModel activation) {
        ActivationDTO dto = activation.commit();
        activationClient.delete(dto);
        notificationService.cancelNotification(activation);
    }

    public void updateActivation(ActivationModel act, ApplianceModel app, LocalDateTime time) {
        act.setAppliance(app);
        act.setActivationTime(time);
        ActivationDTO dto = act.commit();
        activationClient.save(dto);
        notificationService.rescheduleNotification(act);
    }
}
