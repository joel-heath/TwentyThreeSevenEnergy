package uk.ac.soton.comp2300.group42.metric;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import uk.ac.soton.comp2300.group42.common.EnergyCategory;

public record SaveMetricRequest(
        @NotNull(message = "Energy used must not be null")
        @Min(value = 0, message = "Energy used must be a non-negative number")
        Double energyUsed,

        @NotNull(message = "Energy price must not be null")
        @Min(value = 0, message = "Energy price must be a non-negative number")
        Double energyPrice,

        @NotNull(message = "Energy category must not be null")
        EnergyCategory category
) {}
