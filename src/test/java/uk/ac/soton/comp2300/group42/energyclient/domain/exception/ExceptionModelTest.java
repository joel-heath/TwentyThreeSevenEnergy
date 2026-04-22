package uk.ac.soton.comp2300.group42.energyclient.domain.exception;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionModelTest {

    @Test
    void apiException_exposesAllFields() {
        Instant timestamp = Instant.parse("2026-04-19T10:15:30Z");
        ApiException ex = new ApiException(timestamp, 422, "Unprocessable Entity", "details", "/path");

        assertEquals(timestamp, ex.getTimestamp());
        assertEquals(422, ex.getStatus());
        assertEquals("Unprocessable Entity", ex.getError());
        assertEquals("details", ex.getMessage());
        assertEquals("/path", ex.getPath());
    }

    @Test
    void typedApiExceptions_haveFixedStatusCodes() {
        Instant timestamp = Instant.now();

        assertEquals(400, new BadRequestException(timestamp, "e", "m", "p").getStatus());
        assertEquals(401, new UnauthorizedException(timestamp, "e", "m", "p").getStatus());
        assertEquals(403, new ForbiddenException(timestamp, "e", "m", "p").getStatus());
        assertEquals(404, new NotFoundException(timestamp, "e", "m", "p").getStatus());
        assertEquals(409, new ConflictException(timestamp, "e", "m", "p").getStatus());
        assertEquals(500, new InternalServerErrorException(timestamp, "e", "m", "p").getStatus());
    }

    @Test
    void networkException_constructors_setMessageAndCause() {
        Throwable cause = new IllegalStateException("inner");
        NetworkException withCause = new NetworkException("network", cause);
        NetworkException messageOnly = new NetworkException("network-only");

        assertEquals("network", withCause.getMessage());
        assertSame(cause, withCause.getCause());
        assertEquals("network-only", messageOnly.getMessage());
        assertNull(messageOnly.getCause());
    }

    @Test
    void dataFetchException_constructors_setMessageAndCause() {
        Throwable cause = new IllegalArgumentException("inner");
        DataFetchException withCause = new DataFetchException("fetch", cause);
        DataFetchException messageOnly = new DataFetchException("fetch-only");

        assertEquals("fetch", withCause.getMessage());
        assertSame(cause, withCause.getCause());
        assertEquals("fetch-only", messageOnly.getMessage());
        assertNull(messageOnly.getCause());
    }
}
