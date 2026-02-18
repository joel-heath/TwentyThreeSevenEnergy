package uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel;

import com.google.inject.Inject;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.util.Duration;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.EnergyCalculator;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.PreferencesModel;

public class EnergyUsageViewModel {

    private final EnergyCalculator calc;
    private final PreferencesModel preferences;

    private final StringProperty costMessage = new SimpleStringProperty("£0.00");
    private final StringProperty goalMessage = new SimpleStringProperty("Goal: £1.00");
    private final DoubleProperty usage = new SimpleDoubleProperty(0);
    private final DoubleProperty cost = new SimpleDoubleProperty(0);
    private final IntegerProperty counter = new SimpleIntegerProperty(0);

    @Inject public EnergyUsageViewModel(EnergyCalculator calc, PreferencesModel preferences) {
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
    public PreferencesModel getPreferences() {return preferences;}
}
