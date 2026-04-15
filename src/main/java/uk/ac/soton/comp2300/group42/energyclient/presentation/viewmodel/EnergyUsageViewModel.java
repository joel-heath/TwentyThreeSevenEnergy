package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import com.google.inject.Inject;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import uk.ac.soton.comp2300.group42.energyclient.di.qualifier.UIExecutor;
import uk.ac.soton.comp2300.group42.energyclient.domain.service.EnergyCalculator;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;
import uk.ac.soton.comp2300.group42.preferences.ColorVision;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EnergyUsageViewModel {

    public enum UsageState { NORMAL, WARNING, CRITICAL }

    private final EnergyCalculator calc;
    private final ObservablePreferences preferences;
    private final Executor uiExecutor;

    private final StringProperty costMessage = new SimpleStringProperty("£0.00");
    private final StringProperty goalMessage = new SimpleStringProperty("Goal: £1.00");
    private final DoubleProperty usage = new SimpleDoubleProperty(0);
    private final DoubleProperty cost = new SimpleDoubleProperty(0);
    private final IntegerProperty counter = new SimpleIntegerProperty(0);

    private final ObjectProperty<UsageState> usageState = new SimpleObjectProperty<>(UsageState.NORMAL);

    private ScheduledExecutorService testTimer;

    @Inject public EnergyUsageViewModel(EnergyCalculator calc, ObservablePreferences preferences, @UIExecutor Executor uiExecutor) {
        this.calc = calc;
        this.preferences = preferences;
        this.uiExecutor = uiExecutor;
        goalMessage.bind(preferences.energyGoalProperty().map(goal -> String.format("Goal: £%.2f", goal.doubleValue())));
        usage.bind(Bindings.createDoubleBinding(
                () -> {
                    double target = preferences.getEnergyGoal();
                    if (target == 0) return 0.0;
                    return Math.clamp(cost.get() / target, 0, 2);
                },
                cost, preferences.energyGoalProperty()
        ));
        usageState.bind(Bindings.createObjectBinding(() -> {
            double currentUsage = usage.get();
            if (currentUsage >= 2.0) return UsageState.CRITICAL;
            if (currentUsage >= 1.5) return UsageState.WARNING;
            return UsageState.NORMAL;
        }, usage));
    }

    public void recalculateCost() {
        int joules = 1 + 5 * counter.get();
        double pounds = calc.convertJoulesToPounds(joules);
        cost.set(pounds);
        costMessage.set(String.format("£%.2f", pounds));
    }

    public void startAutoUpdateTest() {
        if (testTimer != null) return;
        testTimer = Executors.newSingleThreadScheduledExecutor();
        testTimer.scheduleAtFixedRate(() ->
            uiExecutor.execute(() -> {
                counter.set(counter.get() + 1);
                recalculateCost();
            }), 1, 1, TimeUnit.SECONDS);
    }

    public void setCost(double pounds) {
        cost.set(pounds);
        costMessage.set(String.format("£%.2f", pounds));
    }

    public DoubleProperty usageProperty() {return usage;}
    public StringProperty costMessageProperty() {return costMessage;}
    public StringProperty goalMessageProperty() {return goalMessage;}
    public ObservablePreferences getPreferences() {return preferences;}
    public ObjectProperty<UsageState> usageStateProperty() { return usageState; }
    public ObjectProperty<ColorVision> visionProperty() { return preferences.visionProperty(); }
}