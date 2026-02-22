package uk.ac.soton.comp2300.group42.user;

public record RegistrationRequest(
        String name,
        String email,
        String password
) {}