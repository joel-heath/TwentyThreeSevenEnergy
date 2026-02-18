package uk.ac.soton.comp2300.group42.energyclient.ui.model;

import com.google.inject.Singleton;

@Singleton
public class EnergyCalculator {

    public double convertJoulesToPounds(int joules) {
        // arbitrary formula: £ = joules * 0.00042
        return joules * 0.00042;
    }
}
