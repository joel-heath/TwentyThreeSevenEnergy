package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import uk.ac.soton.comp2300.group42.energyclient.domain.exception.*;

import java.time.Instant;

public class LocalRepositoryUtils {

    public static void throwApiException(int status, String message) throws ApiException {
        Instant timestamp = Instant.now();
        String path = "local://repository";

        switch (status) {
            case 400 -> throw new BadRequestException(timestamp, "Bad Request", message, path);
            case 401 -> throw new UnauthorizedException(timestamp, "Unauthorized", message, path);
            case 403 -> throw new ForbiddenException(timestamp, "Forbidden", message, path);
            case 404 -> throw new NotFoundException(timestamp, "Not Found", message, path);
            case 409 -> throw new ConflictException(timestamp, "Conflict", message, path);
            case 500 -> throw new InternalServerErrorException(timestamp, "Internal Server Error", message, path);
            default -> throw new ApiException(timestamp, status, "Error", message, path);
        }

    }
}
