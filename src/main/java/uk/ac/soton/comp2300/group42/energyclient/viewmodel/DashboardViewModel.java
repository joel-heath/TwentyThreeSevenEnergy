package uk.ac.soton.comp2300.group42.energyclient.viewmodel;

import javafx.beans.property.*;
import javafx.collections.transformation.SortedList;
import uk.ac.soton.comp2300.group42.energyclient.model.EnergyCalculator;
import uk.ac.soton.comp2300.group42.energyclient.model.entity.Activation;
import uk.ac.soton.comp2300.group42.energyclient.model.repository.ActivationRepository;

import java.util.Comparator;

public class DashboardViewModel {

    private final IntegerProperty counter = new SimpleIntegerProperty(0);
    private final StringProperty cost = new SimpleStringProperty("£0.00");
    private final DoubleProperty costVal = new SimpleDoubleProperty(0);
    private final StringProperty goalStr = new SimpleStringProperty("£0.00");
    private final DoubleProperty costGoal = new SimpleDoubleProperty(0);
    private final DoubleProperty usage = new SimpleDoubleProperty(0);

    private final SortedList<Activation> activations;

    private final EnergyCalculator calc;

    public DashboardViewModel(EnergyCalculator calc, ActivationRepository activationRepo) {
        this.calc = calc;

        activations = new SortedList<>(activationRepo.getActivations());
        activations.setComparator(Comparator.comparing(Activation::getActivationTime));
    }

    public IntegerProperty counterProperty() { return counter; }
    public StringProperty costProperty() { return cost; }
    public DoubleProperty usageProperty() { return usage; }
    public StringProperty goalProperty() { return goalStr; }
    public SortedList<Activation> getActivations() { return activations; }

    public void incrementCounter() {
        counter.set(counter.get() + 1);
    }

    public void recalculateCost() {
        int joules = 100 + 500 * counter.get();
        double pounds = calc.convertJoulesToPounds(joules);
        costVal.set(pounds);
        cost.set(String.format("£%.2f", pounds));
        recalculateUsage();
    }

    public void setCostGoal(double goal) {
        costGoal.set(goal);
        goalStr.set(String.format("%.2f", goal));
        recalculateUsage();
    }

    public void recalculateUsage() {
        double cost = costVal.get();
        double target = costGoal.get();
        double percentUsage = cost / target;
        usage.set(percentUsage);
    }
}
