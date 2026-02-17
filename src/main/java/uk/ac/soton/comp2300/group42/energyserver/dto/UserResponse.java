package uk.ac.soton.comp2300.group42.energyserver.dto;

import uk.ac.soton.comp2300.group42.energyserver.model.User;

public record UserResponse(
        Long id,
        String name,
        String email) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }
}