package uk.ac.soton.comp2300.group42.energyclient.domain.exception;

import java.time.Instant;

public class InternalServerErrorException extends ApiException {

    public InternalServerErrorException(Instant timestamp, String error, String message, String path) {
        super(timestamp, 500, error, message, path);
    }
}
