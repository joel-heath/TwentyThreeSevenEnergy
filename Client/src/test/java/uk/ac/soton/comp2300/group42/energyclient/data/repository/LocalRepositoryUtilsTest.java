package uk.ac.soton.comp2300.group42.energyclient.data.repository;

import org.junit.jupiter.api.Test;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.*;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class LocalRepositoryUtilsTest {

    @Test
    void throwApiException_mapsKnownStatusesToSpecificExceptionTypes() {
        assertMapped(400, BadRequestException.class, "Bad Request");
        assertMapped(401, UnauthorizedException.class, "Unauthorized");
        assertMapped(403, ForbiddenException.class, "Forbidden");
        assertMapped(404, NotFoundException.class, "Not Found");
        assertMapped(409, ConflictException.class, "Conflict");
        assertMapped(500, InternalServerErrorException.class, "Internal Server Error");
    }

    @Test
    void throwApiException_forUnknownStatus_throwsGenericApiException() {
        Instant before = Instant.now();
        ApiException ex = assertThrows(ApiException.class,
                () -> LocalRepositoryUtils.throwApiException(418, "teapot"));
        Instant after = Instant.now();

        assertEquals(418, ex.getStatus());
        assertEquals("Error", ex.getError());
        assertEquals("teapot", ex.getMessage());
        assertEquals("local://repository", ex.getPath());
        assertFalse(ex.getTimestamp().isBefore(before));
        assertFalse(ex.getTimestamp().isAfter(after));
    }

    @Test
    void throwApiException_handlesBoundaryAndUnexpectedStatuses() {
        assertGenericStatus(0);
        assertGenericStatus(399);
        assertGenericStatus(402);
        assertGenericStatus(600);
        assertGenericStatus(-1);
    }

    private static void assertMapped(int status, Class<? extends ApiException> expectedType, String expectedError) {
        Instant before = Instant.now();
        ApiException ex = assertThrows(expectedType,
                () -> LocalRepositoryUtils.throwApiException(status, "msg"));
        Instant after = Instant.now();

        assertEquals(status, ex.getStatus());
        assertEquals(expectedError, ex.getError());
        assertEquals("msg", ex.getMessage());
        assertEquals("local://repository", ex.getPath());
        assertFalse(ex.getTimestamp().isBefore(before));
        assertFalse(ex.getTimestamp().isAfter(after));
    }

    private static void assertGenericStatus(int status) {
        ApiException ex = assertThrows(ApiException.class,
                () -> LocalRepositoryUtils.throwApiException(status, "x"));
        assertEquals(status, ex.getStatus());
        assertEquals("Error", ex.getError());
    }
}
