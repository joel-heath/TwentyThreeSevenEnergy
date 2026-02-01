package uk.ac.soton.comp2300.group42.energyclient.viewmodel;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.util.Duration;
import uk.ac.soton.comp2300.group42.energyclient.model.EnergyCalculator;
import uk.ac.soton.comp2300.group42.energyclient.model.entity.Activation;
import uk.ac.soton.comp2300.group42.energyclient.model.entity.Appliance;
import uk.ac.soton.comp2300.group42.energyclient.model.repository.ActivationRepository;
import uk.ac.soton.comp2300.group42.energyclient.model.repository.ApplianceRepository;
import uk.ac.soton.comp2300.group42.energyclient.services.NotificationService;
import java.time.LocalDateTime;
import java.util.Comparator;

public class DashboardViewModel {

    private final IntegerProperty counter = new SimpleIntegerProperty(0);
    private final StringProperty costMessage = new SimpleStringProperty("Total Spent: £0.00");
    private final DoubleProperty cost = new SimpleDoubleProperty(0);
    private final StringProperty goalMessage = new SimpleStringProperty("Cost Goal: £1.00");
    private final DoubleProperty costGoal = new SimpleDoubleProperty(1);
    private final DoubleProperty usage = new SimpleDoubleProperty(0);

    private final ApplianceRepository applianceRepo;
    private final ActivationRepository activationRepo;
    private final NotificationService notificationService;
    private final ObservableList<Appliance> appliances = FXCollections.observableArrayList();
    private final SortedList<Activation> activations;

    private final EnergyCalculator calc;

    public DashboardViewModel(EnergyCalculator calc, ActivationRepository activationRepo, ApplianceRepository applianceRepo, NotificationService notificationService) {
        this.calc = calc;
        this.activationRepo = activationRepo;
        this.applianceRepo = applianceRepo;
        this.notificationService = notificationService;
        activations = new SortedList<>(activationRepo.getActivations());
        activations.setComparator(Comparator.comparing(Activation::getActivationTime));
        loadAppliances();
    }

    private void loadAppliances() {
        var data = applianceRepo.findAll();
        appliances.addAll(data);
    }

    public ObservableList<Appliance> getAppliances() { return appliances; }

    public IntegerProperty counterProperty() { return counter; }
    public DoubleProperty usageProperty() { return usage; }
    public StringProperty costMessageProperty() { return costMessage; }
    public StringProperty goalMessageProperty() { return goalMessage; }
    public SortedList<Activation> getActivations() { return activations; }

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

    public void removeActivation(Activation activation) {
        activationRepo.delete(activation);
        notificationService.cancelNotification(activation);
    }

    public void updateActivation(Activation act, Appliance app, LocalDateTime time) {
        act.setAppliance(app);
        act.setActivationTime(time);
        activationRepo.save(act);
        notificationService.rescheduleNotification(act);
    }
}
