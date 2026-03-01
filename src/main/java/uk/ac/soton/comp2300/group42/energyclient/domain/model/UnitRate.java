package uk.ac.soton.comp2300.group42.energyclient.domain.model;

import java.time.ZonedDateTime;

public record UnitRate(
        double valueIncVat,
        ZonedDateTime validFrom
) {
    public PriceStatus getPriceStatus() {
        return PriceStatus.fromValue(valueIncVat);
    }
}

