package uk.ac.soton.comp2312.group42.energyclient.model;

public class EnergyCalculator {

    public double convertJoulesToPounds(int joules) {
        // arbitrary formula: £ = joules * 0.00042
        return joules * 0.00042;
    }
}
