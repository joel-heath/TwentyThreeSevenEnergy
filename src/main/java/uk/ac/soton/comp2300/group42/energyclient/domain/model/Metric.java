package uk.ac.soton.comp2300.group42.energyclient.domain.model;

import uk.ac.soton.comp2300.group42.common.EnergyCategory;

import java.time.LocalDateTime;

public record Metric(
    Long id,
    Long houseId,
    LocalDateTime dateTime,
    Double energyUsed,
    EnergyCategory category
) {}
