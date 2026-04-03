package uk.ac.soton.comp2300.group42.energyprice;

import java.time.LocalDateTime;

public record EnergyPriceResponse(
        Long id,
        LocalDateTime validFrom,
        LocalDateTime validTo,
        Double pricePerKwh
) {}
