package uk.ac.soton.comp2300.group42.energyclient.domain.exception;

public class UnauthorizedException extends ApiException {
    public UnauthorizedException(String message) {
        super(message, 401);
    }
}
