package uk.ac.soton.comp2300.group42.housemate;

import uk.ac.soton.comp2300.group42.common.Role;

public record HousemateRequest(

        String email,
        Role role
) {}