package uk.ac.soton.comp2300.group42.appliance;

public record ApplianceResponse(
        Long id,
        Long houseId,
        String name
) {}
