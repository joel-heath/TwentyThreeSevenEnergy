package uk.ac.soton.comp2300.group42.energyclient.domain.model;

import java.time.LocalDateTime;

public record EnergyPrice(
        Long id,
        LocalDateTime validFrom,
        LocalDateTime validTo,
        Double pricePerKwh
) {
}
