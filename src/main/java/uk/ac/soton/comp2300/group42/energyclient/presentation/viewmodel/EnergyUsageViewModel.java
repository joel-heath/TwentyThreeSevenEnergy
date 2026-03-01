package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import com.google.inject.Inject;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.util.Duration;
import uk.ac.soton.comp2300.group42.energyclient.domain.service.EnergyCalculator;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;

public class EnergyUsageViewModel {

    private final EnergyCalculator calc;
    private final ObservablePreferences preferences;

    private final StringProperty costMessage = new SimpleStringProperty("£0.00");
    private final StringProperty goalMessage = new SimpleStringProperty("Goal: £1.00");
    private final DoubleProperty usage = new SimpleDoubleProperty(0);
    private final DoubleProperty cost = new SimpleDoubleProperty(0);
    private final IntegerProperty counter = new SimpleIntegerProperty(0);

    @Inject
    public EnergyUsageViewModel(EnergyCalculator calc, ObservablePreferences preferences) {
        this.calc = calc;
        this.preferences = preferences;

        goalMessage.bind(
                preferences.energyGoalProperty().map(goal -> String.format("Goal: £%.2f", goal.doubleValue()))
        );
        usage.bind(Bindings.createDoubleBinding(
                () -> {
                    double target = preferences.getEnergyGoal();
                    if (target == 0) return 0.0;
                    return Math.max(0, Math.min(cost.get() / target, 1));
                },
                cost, preferences.energyGoalProperty()
        ));
    }

    public void recalculateCost() {
        int joules = 1 + 5 * counter.get();
        double pounds = calc.convertJoulesToPounds(joules);
        cost.set(pounds);
        costMessage.set(String.format("£%.2f", pounds));
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

    public void setCost(double pounds) {
        cost.set(pounds);
        costMessage.set(String.format("£%.2f", pounds));
    }

    public DoubleProperty usageProperty() {return usage;}
    public StringProperty costMessageProperty() {return costMessage;}
    public StringProperty goalMessageProperty() {return goalMessage;}
    public ObservablePreferences getPreferences() {return preferences;}
}
