package uk.ac.soton.comp2300.group42.user;

public record LoginRequest(
        String email,
        String password
) {}