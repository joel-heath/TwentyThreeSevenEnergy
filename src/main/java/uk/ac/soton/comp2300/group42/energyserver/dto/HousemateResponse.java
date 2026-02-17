package uk.ac.soton.comp2300.group42.energyserver.dto;

import uk.ac.soton.comp2300.group42.energyserver.model.House;
import uk.ac.soton.comp2300.group42.energyserver.model.HouseMembership;
import uk.ac.soton.comp2300.group42.energyserver.model.Role;
import uk.ac.soton.comp2300.group42.energyserver.model.User;

public record HousemateResponse(
        Long id,
        String name,
        String email,
        Long houseId,
        Role role) {

    public static HousemateResponse from(HouseMembership membership) {
        User user = membership.getUser();
        House house = membership.getHouse();
        Role role = membership.getRole();
        return new HousemateResponse(user.getId(), user.getName(), user.getEmail(), house.getId(), role);
    }
}