package uk.ac.soton.comp2300.group42.energyclient.presentation.model;

public class EnergyPriceModel {
    private final double price;   // pence per kWh
    private final String validFrom;

    public EnergyPriceModel(double price, String validFrom) {
        this.price = price;
        this.validFrom = validFrom;
    }

    public double getPrice() {
        return price;
    }

    public String getValidFrom() {
        return validFrom;
    }

}
