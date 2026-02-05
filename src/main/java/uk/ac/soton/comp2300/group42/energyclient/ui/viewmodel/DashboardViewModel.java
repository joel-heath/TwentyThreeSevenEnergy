package uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.util.Duration;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.ActivationModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.ApplianceModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.EnergyCalculator;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.Repository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;

public class DashboardViewModel {

    private final IntegerProperty counter = new SimpleIntegerProperty(0);
    private final StringProperty costMessage = new SimpleStringProperty("Total Spent: £0.00");
    private final DoubleProperty cost = new SimpleDoubleProperty(0);
    private final StringProperty goalMessage = new SimpleStringProperty("Cost Goal: £1.00");
    private final DoubleProperty costGoal = new SimpleDoubleProperty(1);
    private final DoubleProperty usage = new SimpleDoubleProperty(0);

    private final Repository repository;
    private final ObservableList<ApplianceModel> appliances;
    private final SortedList<ActivationModel> activations;

    private final EnergyCalculator calc;

    public DashboardViewModel(Repository repository, EnergyCalculator calc) {
        this.repository = repository;
        this.calc = calc;

        this.appliances = repository.getAppliances();
        this.activations = new SortedList<>(repository.getActivations());
        this.activations.setComparator(Comparator.comparing(ActivationModel::getActivationTime));

        CompletableFuture.runAsync(repository::fetchAllData); // Run on a background thread so UI doesn't hang if the API is slow
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
        repository.deleteActivation(activation);
    }

    public void updateActivation(ActivationModel act, ApplianceModel app, LocalDateTime time) {
        act.setAppliance(app);
        act.setActivationTime(time);
        repository.saveActivation(act);
    }
}
