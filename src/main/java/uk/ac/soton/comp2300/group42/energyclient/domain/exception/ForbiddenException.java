package uk.ac.soton.comp2300.group42.energyclient.domain.exception;

import java.time.Instant;

public class ForbiddenException extends ApiException {

    public ForbiddenException(Instant timestamp, String error, String message, String path) {
        super(timestamp, 403, error, message, path);
    }
}
