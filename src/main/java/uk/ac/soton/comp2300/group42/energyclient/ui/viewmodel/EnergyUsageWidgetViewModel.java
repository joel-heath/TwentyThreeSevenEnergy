package uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.PreferencesModel;

public class EnergyUsageWidgetViewModel {
    private final StringProperty costMessage = new SimpleStringProperty("£0.00");
    private final StringProperty goalMessage = new SimpleStringProperty("Goal: £1.00");
    private final DoubleProperty usage = new SimpleDoubleProperty(0);
    private final DoubleProperty cost = new SimpleDoubleProperty(0);

    private final PreferencesModel preferences;

    public EnergyUsageWidgetViewModel(PreferencesModel preferences) {
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

    public void setCost(double pounds) {
        cost.set(pounds);
        costMessage.set(String.format("£%.2f", pounds));
    }

    public DoubleProperty usageProperty() {return usage;}
    public StringProperty costMessageProperty() {return costMessage;}
    public StringProperty goalMessageProperty() {return goalMessage;}
    public PreferencesModel getPreferences() {return preferences;}
}
