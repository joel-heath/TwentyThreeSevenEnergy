package uk.ac.soton.comp2300.group42.energyserver.exception;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {
        super(message);
    }
}
