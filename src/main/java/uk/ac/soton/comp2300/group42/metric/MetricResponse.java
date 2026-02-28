package uk.ac.soton.comp2300.group42.metric;

import java.time.LocalDate;

public record MetricResponse(
        Long id,
        Long houseId,
        LocalDate date,
        Double energyUsed
) {}
