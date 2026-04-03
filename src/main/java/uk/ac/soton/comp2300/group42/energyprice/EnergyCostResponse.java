package uk.ac.soton.comp2300.group42.energyprice;

import java.time.LocalDateTime;

public record EnergyCostResponse(
        LocalDateTime timestamp,
        Double energyUsed,
        Double unitPrice,
        Double totalCost
) {}
