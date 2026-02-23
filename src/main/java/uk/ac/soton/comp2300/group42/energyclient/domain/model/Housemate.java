package uk.ac.soton.comp2300.group42.energyclient.domain.model;

import uk.ac.soton.comp2300.group42.common.Role;

public record Housemate(
        Long userId,
        Long houseId,
        Role role
) {}
