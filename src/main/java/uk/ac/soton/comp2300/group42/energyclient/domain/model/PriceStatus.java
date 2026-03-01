package uk.ac.soton.comp2300.group42.energyclient.domain.model;

public enum PriceStatus {
    CHEAP, AVERAGE, EXPENSIVE;

    public static PriceStatus fromValue(double valueIncVat) {
        if (valueIncVat < 10) return CHEAP;
        if (valueIncVat < 25) return AVERAGE;
        return EXPENSIVE;
    }
}
