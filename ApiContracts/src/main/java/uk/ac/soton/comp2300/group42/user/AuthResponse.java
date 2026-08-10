package uk.ac.soton.comp2300.group42.user;

public record AuthResponse(
        String accessToken,
        String refreshToken
) {}
