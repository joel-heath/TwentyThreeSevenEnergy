package uk.ac.soton.comp2300.group42.user;

public record UserResponse(
        Long id,
        String name,
        String email
) {}