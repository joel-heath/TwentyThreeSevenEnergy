package uk.ac.soton.comp2300.group42.energyclient.domain.model;

import java.time.LocalDate;

public record Metric(
    Long id,
    Long houseId,
    LocalDate date,
    Double energyUsed
) {}
