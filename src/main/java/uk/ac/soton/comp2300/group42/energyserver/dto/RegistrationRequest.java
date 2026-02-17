package uk.ac.soton.comp2300.group42.energyserver.dto;

public record RegistrationRequest(
        String name,
        String email,
        String password) {}