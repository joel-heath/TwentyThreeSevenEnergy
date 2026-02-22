package uk.ac.soton.comp2300.group42.house;

import java.time.ZoneId;

public record UpdateHouseRequest(
        String name,
        String address,
        ZoneId timezone
) {}
