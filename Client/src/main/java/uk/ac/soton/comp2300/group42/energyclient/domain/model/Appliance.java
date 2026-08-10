package uk.ac.soton.comp2300.group42.energyclient.domain.model;

public record Appliance(
        Long id,
        Long houseId,
        String name
) {}
