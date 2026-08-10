package uk.ac.soton.comp2300.group42.energyclient.domain.exception;

public class DataFetchException extends RuntimeException {

    public DataFetchException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataFetchException(String message) {
        super(message);
    }
}
