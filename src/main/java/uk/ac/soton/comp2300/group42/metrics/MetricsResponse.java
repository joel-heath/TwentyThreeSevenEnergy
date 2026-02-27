package uk.ac.soton.comp2300.group42.metrics;

import java.util.List;

public record MetricsResponse(
        Long id,
        Long houseId,
        List<Double> metrics
) {}
