package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.debug;

import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.EnergyCalculator;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;
import uk.ac.soton.comp2300.group42.energyclient.presentation.store.UserStore;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.DoubleUnaryOperator;

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

    private DoubleUnaryOperator formula = x -> x * 500 + 100;

    private final EnergyCalculator calc;
    private final UserStore userStore;
    private final ObservablePreferences preferences;

    @Inject public DashboardDebugViewModel(UserStore userStore, EnergyCalculator calc) {
        //costMessage.bind(Bindings.format("%£.2f", cost.get()));
        //goalMessage.bind(Bindings.format("%£.2f", goal.get()));
        this.userStore = userStore;
        this.calc = calc;
        this.preferences = userStore.getPreferences();

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
        int joules = (int) formula.applyAsDouble(counter.get());
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

    public ObservablePreferences getPreferences() {
        return preferences;
    }

    public void setFormula(DoubleUnaryOperator formula) {
        this.formula = formula;
    }


    public DoubleUnaryOperator getFormula() {
        return formula;
    }

    public void save() { userStore.savePreferences(); }

}
