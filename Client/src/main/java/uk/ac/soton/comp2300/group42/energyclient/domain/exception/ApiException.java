package uk.ac.soton.comp2300.group42.energyclient.domain.exception;

import java.time.Instant;

public class ApiException extends RuntimeException {

    private final Instant timestamp;
    private final int status;
    private final String error;
    private final String path;

    public ApiException(Instant timestamp, int status, String error, String message, String path) {
        super(message);
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.path = path;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getPath() {
        return path;
    }
}
