package uk.ac.soton.comp2300.group42.energyclient.domain.model;

import uk.ac.soton.comp2300.group42.common.Role;

import java.time.ZoneId;

public record House(
        Long id,
        String name,
        String address,
        ZoneId timezone,
        Role role
) {}
