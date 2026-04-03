package uk.ac.soton.comp2300.group42.energyprice;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record SaveEnergyPriceRequest(
        @NotNull LocalDateTime validFrom,
        @NotNull LocalDateTime validTo,
        @NotNull Double priceIncVat
) {}
