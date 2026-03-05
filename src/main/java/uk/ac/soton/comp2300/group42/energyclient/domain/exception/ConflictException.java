package uk.ac.soton.comp2300.group42.energyclient.domain.exception;

import java.time.Instant;

public class ConflictException extends ApiException {

    public ConflictException(Instant timestamp, String error, String message, String path) {
        super(timestamp, 409, error, message, path);
    }
}
