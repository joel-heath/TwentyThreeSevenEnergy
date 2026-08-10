package uk.ac.soton.comp2300.group42.house;

import uk.ac.soton.comp2300.group42.common.Role;

import java.time.ZoneId;

public record HouseResponse(
        Long id,
        String name,
        String address,
        ZoneId timezone,
        Role role
) {}
