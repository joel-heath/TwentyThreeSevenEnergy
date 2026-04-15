package uk.ac.soton.comp2300.group42.energyclient.domain.model;

import java.time.LocalDateTime;

public record EnergyCost(
        LocalDateTime timestamp,
        Double energyUsed,
        Double unitPrice,
        Double totalCost
) {
}
