package uk.ac.soton.comp2300.group42.energyclient.data.dto;

public record AuthResponseDTO(
        String accessToken,
        String refreshToken
) {}
