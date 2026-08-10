package uk.ac.soton.comp2300.group42.energyclient.domain.exception;

public class NetworkException extends RuntimeException {

    public NetworkException(String message, Throwable cause) {
        super(message, cause);
    }

    public NetworkException(String message) {
        super(message);
    }
}
