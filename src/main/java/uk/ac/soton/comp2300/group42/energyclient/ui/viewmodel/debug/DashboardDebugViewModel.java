package uk.ac.soton.comp2300.group42.energyclient.ui.viewmodel.debug;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import uk.ac.soton.comp2300.group42.energyclient.ui.model.EnergyCalculator;

public class DashboardDebugViewModel {

    private final IntegerProperty counter = new SimpleIntegerProperty(0);
    private final DoubleProperty usage = new SimpleDoubleProperty(0);
    private final DoubleProperty cost = new SimpleDoubleProperty(0);
    private final StringProperty costMessage = new SimpleStringProperty("Total Spent: £0.00");
    private final DoubleProperty goal = new SimpleDoubleProperty(1);
    private final StringProperty goalMessage = new SimpleStringProperty("Goal: £1.00");

    private final EnergyCalculator calc;

    public DashboardDebugViewModel(EnergyCalculator calc) {
        //costMessage.bind(Bindings.format("%£.2f", cost.get()));
        //goalMessage.bind(Bindings.format("%£.2f", goal.get()));
        this.calc = calc;
        usage.bind(Bindings.when(goal.isEqualTo(0))
                .then(0.0)
                .otherwise(cost.divide(goal)));
    }

    public DoubleProperty usageProperty() { return usage; }
    public IntegerProperty counterProperty() { return counter; }
    public StringProperty costMessageProperty() { return costMessage; }
    public StringProperty goalMessageProperty() { return goalMessage; }

    public void incrementCounter() {
        counter.set(counter.get() + 1);
        recalculateCost();
    }

    public void decrementCounter() {
        counter.set(counter.get() - 1);
        recalculateCost();
    }



    public void recalculateCost() {
        int joules = 100 + 500 * counter.get();
        double pounds = calc.convertJoulesToPounds(joules);
        cost.set(pounds);
        costMessage.set(String.format("Total Spent: £%.2f", pounds));
    }

}
