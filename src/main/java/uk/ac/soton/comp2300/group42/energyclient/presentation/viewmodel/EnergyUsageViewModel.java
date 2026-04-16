package uk.ac.soton.comp2300.group42.energyclient.presentation.viewmodel;

import com.google.inject.Inject;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import uk.ac.soton.comp2300.group42.energyclient.domain.model.EnergyCost;
import uk.ac.soton.comp2300.group42.energyclient.domain.repository.EnergyPriceRepository;
import uk.ac.soton.comp2300.group42.energyclient.presentation.observable.ObservablePreferences;
import uk.ac.soton.comp2300.group42.preferences.ColorVision;

import java.time.LocalDate;

public class EnergyUsageViewModel {

    public enum UsageState { NORMAL, WARNING, CRITICAL }

    private final EnergyPriceRepository energyPriceRepo;
    private final ObservablePreferences preferences;

    private final StringProperty costMessage = new SimpleStringProperty("£0.00");
    private final StringProperty goalMessage = new SimpleStringProperty("Goal: £1.00");
    private final DoubleProperty usage = new SimpleDoubleProperty(0);
    private final DoubleProperty cost = new SimpleDoubleProperty(0);

    private final ObjectProperty<UsageState> usageState = new SimpleObjectProperty<>(UsageState.NORMAL);

    @Inject public EnergyUsageViewModel(EnergyPriceRepository energyPriceRepo, ObservablePreferences preferences) {
        this.energyPriceRepo = energyPriceRepo;
        this.preferences = preferences;
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

        recalculateCost();
    }

    public void recalculateCost() {
        double pounds = energyPriceRepo.getCostsForDate(preferences.getActiveHouse().getId(), LocalDate.now()).stream()
                .mapToDouble(EnergyCost::totalCost)
                .sum() / 100.0; // totalCost is in pence, convert to pounds
        cost.set(pounds);
        costMessage.set(String.format("£%.2f", pounds));
    }

    public void setCost(double pounds) {
        cost.set(pounds);
        costMessage.set(String.format("£%.2f", pounds));
    }

    public DoubleProperty usageProperty() { return usage; }
    public StringProperty costMessageProperty() { return costMessage; }
    public StringProperty goalMessageProperty() { return goalMessage; }
    public ObservablePreferences getPreferences() { return preferences; }
    public ObjectProperty<UsageState> usageStateProperty() { return usageState; }
    public ObjectProperty<ColorVision> visionProperty() { return preferences.visionProperty(); }
}