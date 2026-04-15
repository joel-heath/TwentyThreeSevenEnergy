package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel.debug;

import com.google.inject.Inject;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import uk.ac.soton.comp2300.group42.energyclient.di.qualifier.UIExecutor;
import uk.ac.soton.comp2300.group42.energyclient.domain.service.EnergyCalculator;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;
import uk.ac.soton.comp2300.group42.energyclient.presentation.store.UserStore;
import uk.ac.soton.comp2300.group42.preferences.ColorVision;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.DoubleUnaryOperator;

import static java.lang.Math.max;

public class DashboardDebugViewModel {

    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor(runnable -> {
                Thread thread = new Thread(runnable);
                thread.setDaemon(true);
                thread.setName("DebugDashboard-Scheduler");
                return thread;
            });

    private final IntegerProperty counter = new SimpleIntegerProperty(0);
    private final DoubleProperty usage = new SimpleDoubleProperty(0);
    private final DoubleProperty cost = new SimpleDoubleProperty(0);
    private final StringProperty costMessage = new SimpleStringProperty("Total Spent: £0.00");
    private final DoubleProperty goal = new SimpleDoubleProperty(1);
    private final StringProperty goalMessage = new SimpleStringProperty("Goal: £1.00");

    private final StringProperty costGoalInput = new SimpleStringProperty("");
    private final BooleanProperty hasCostGoalError = new SimpleBooleanProperty(false);
    private final ObjectProperty<LocalDate> resetDate = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalTime> resetTime = new SimpleObjectProperty<>(LocalTime.now().withSecond(0).withNano(0));
    private final ObservableList<ColorVision> availableVisions = FXCollections.observableArrayList(ColorVision.values());

    private DoubleUnaryOperator formula = x -> x * 500 + 100;

    private final EnergyCalculator calc;
    private final UserStore userStore;
    private final ObservablePreferences preferences;
    private final Executor uiExecutor;

    @Inject public DashboardDebugViewModel(UserStore userStore, EnergyCalculator calc, @UIExecutor Executor uiExecutor) {
        this.userStore = userStore;
        this.calc = calc;
        this.preferences = userStore.getPreferences();
        this.uiExecutor = uiExecutor;

        usage.bind(Bindings.when(goal.isEqualTo(0))
                .then(0.0)
                .otherwise(cost.divide(goal)));
    }

    public void incrementCounter() {
        counter.set(counter.get() + 1);
        recalculateCost();
    }

    public void decrementCounter() {
        counter.set(max(counter.get() - 1, 0));
        recalculateCost();
    }

    public void updateCostGoal() {
        try {
            String text = costGoalInput.get() == null ? "" : costGoalInput.get().replace("£", "").trim();
            double value = Double.parseDouble(text);

            if (value <= 0) throw new NumberFormatException();

            this.goal.set(value);
            goalMessage.set(String.format("Goal: £%.2f", value));

            hasCostGoalError.set(false);
            costGoalInput.set("");

        } catch (NumberFormatException e) {
            hasCostGoalError.set(true);
        }
    }

    public void recalculateCost() {
        int joules = (int) formula.applyAsDouble(counter.get());
        double pounds = calc.convertJoulesToPounds(joules);
        cost.set(pounds);
        costMessage.set(String.format("Total Spent: £%.2f", pounds));
    }

    public void scheduleReset() {
        if (resetDate.get() == null || resetTime.get() == null) return;

        LocalDateTime resetDateTime = LocalDateTime.of(resetDate.get(), resetTime.get());

        long delayMillis = Duration.between(LocalDateTime.now(), resetDateTime).toMillis();

        if (delayMillis <= 0) {
            resetCounter();
            return;
        }

        scheduler.schedule(
                () -> uiExecutor.execute(this::resetCounter),
                delayMillis,
                TimeUnit.MILLISECONDS
        );
    }

    public void resetCounter() {
        counter.set(0);
        recalculateCost();
    }

    public void save() { userStore.savePreferences(); }
    public void setFormula(DoubleUnaryOperator formula) { this.formula = formula; }
    public DoubleUnaryOperator getFormula() { return formula; }

    public IntegerProperty counterProperty() { return counter; }
    public DoubleProperty usageProperty() { return usage; }
    public DoubleProperty costProperty() { return cost; }
    public StringProperty costMessageProperty() { return costMessage; }
    public DoubleProperty goalProperty() { return goal; }
    public StringProperty goalMessageProperty() { return goalMessage; }

    public StringProperty costGoalInputProperty() { return costGoalInput; }
    public BooleanProperty hasCostGoalErrorProperty() { return hasCostGoalError; }
    public ObjectProperty<LocalDate> resetDateProperty() { return resetDate; }
    public ObjectProperty<LocalTime> resetTimeProperty() { return resetTime; }

    public ObjectProperty<ColorVision> visionProperty() { return preferences.visionProperty(); }
    public ObservableList<ColorVision> getAvailableVisions() { return availableVisions; }
}