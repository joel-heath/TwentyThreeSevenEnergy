package uk.ac.soton.comp2300.group42.energyclient.domain.model;

import java.util.List;

public record Metrics(
    Long id,
    Long houseID,
    List<Double> metrics
) {}
