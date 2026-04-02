package uk.ac.soton.comp2300.group42.metric;

import uk.ac.soton.comp2300.group42.common.EnergyCategory;

import java.time.LocalDateTime;

public record MetricResponse(
        Long id,
        Long houseId,
        LocalDateTime dateTime,
        Double energyUsed,
        EnergyCategory category
) {}
