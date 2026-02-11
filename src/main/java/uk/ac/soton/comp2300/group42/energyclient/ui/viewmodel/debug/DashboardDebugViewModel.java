package uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.debug;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.EnergyCalculator;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.PreferencesModel;
import uk.ac.soton.comp2300.group42.energyclient.ui.util.Repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.max;

public class DashboardDebugViewModel {

    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();

    private final IntegerProperty counter = new SimpleIntegerProperty(0);
    private final DoubleProperty usage = new SimpleDoubleProperty(0);
    private final DoubleProperty cost = new SimpleDoubleProperty(0);
    private final StringProperty costMessage = new SimpleStringProperty("Total Spent: £0.00");
    private final DoubleProperty goal = new SimpleDoubleProperty(1);
    private final StringProperty goalMessage = new SimpleStringProperty("Goal: £1.00");

    private final EnergyCalculator calc;
    private final Repository repository;
    private final PreferencesModel preferences;

    public DashboardDebugViewModel(Repository repository, EnergyCalculator calc) {
        //costMessage.bind(Bindings.format("%£.2f", cost.get()));
        //goalMessage.bind(Bindings.format("%£.2f", goal.get()));
        this.repository = repository;
        this.calc = calc;
        this.preferences = repository.getPreferences();

        usage.bind(Bindings.when(goal.isEqualTo(0))
                .then(0.0)
                .otherwise(cost.divide(goal)));
    }

    public IntegerProperty counterProperty() { return counter; }
    public DoubleProperty usageProperty() { return usage; }
    public DoubleProperty costProperty() { return cost; }
    public StringProperty costMessageProperty() { return costMessage; }
    public DoubleProperty goalProperty() { return goal; }
    public StringProperty goalMessageProperty() { return goalMessage; }

    public void incrementCounter() {
        counter.set(counter.get() + 1);
        recalculateCost();
    }

    public void decrementCounter() {
        counter.set(max(counter.get() - 1, 0));
        recalculateCost();
    }

    public void setCostGoal(double goal) {
        this.goal.set(goal);
        goalMessage.set(String.format("Goal: £%.2f", goal));
    }

    public void recalculateCost() {
        int joules = 100 + 500 * counter.get();
        double pounds = calc.convertJoulesToPounds(joules);
        cost.set(pounds);
        costMessage.set(String.format("Total Spent: £%.2f", pounds));
    }

    public void scheduleReset(LocalDateTime resetTime) {
        long delayMillis = Duration.between(
                LocalDateTime.now(),
                resetTime
        ).toMillis();

        if (delayMillis <= 0) {
            counter.set(0);
            return;
        }

        scheduler.schedule(
            () -> Platform.runLater(this::resetCounter),
            delayMillis,
            TimeUnit.MILLISECONDS
        );
    }

    public void resetCounter() {
        counter.set(0);
        recalculateCost();
    }

    public PreferencesModel getPreferences() {
        return preferences;
    }

    public void save() { repository.savePreferences(); }

}
