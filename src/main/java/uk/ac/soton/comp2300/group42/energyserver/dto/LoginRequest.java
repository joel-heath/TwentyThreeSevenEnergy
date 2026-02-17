package uk.ac.soton.comp2300.group42.energyserver.dto;

public record LoginRequest(
        String email,
        String password
) {}