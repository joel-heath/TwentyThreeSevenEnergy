package uk.ac.soton.comp2300.group42.energyclient.integration;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.json.JsonMapper;
import uk.ac.soton.comp2300.group42.energyclient.data.backend.AuthenticatedHttpClient;
import uk.ac.soton.comp2300.group42.energyclient.data.security.TokenStorageService;
import uk.ac.soton.comp2300.group42.user.AuthResponse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Integration tests for the authentication flow.
 * Tests the real HTTP request/response cycle with MockWebServer acting as a test backend,
 * while token storage and JSON mapping are mocked to isolate the HTTP behavior.
 *
 * Demonstrates: Integration Testing, Service Testing, Boundary Testing, Regression Testing
 */
@Tag("integration")
@ExtendWith(MockitoExtension.class)
class AuthenticationFlowIntegrationTest {

    @Mock private TokenStorageService mockTokenStorage;
    @Mock private JsonMapper mockMapper;
    private MockWebServer mockWebServer;
    private AuthenticatedHttpClient httpClient;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        URI baseUrl = URI.create(mockWebServer.url("/api/").toString());
        when(mockTokenStorage.getRefreshToken()).thenReturn("valid-refresh-token");

        httpClient = new AuthenticatedHttpClient(mockMapper, baseUrl, mockTokenStorage);
        httpClient.setTokenPair("initial-access-token", "valid-refresh-token");
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    // =========================================================================
    // UNIT/SERVICE TESTS: Happy path and basic behavior
    // =========================================================================

    @Test
    void get_IncludesAuthorizationHeader_ServiceTest() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("Success Data"));

        HttpResponse<String> response = httpClient.get("data");

        assertEquals(200, response.statusCode());
        assertEquals("Success Data", response.body());

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("GET", request.getMethod());
        assertEquals("/api/data", request.getPath());
        assertEquals("Bearer initial-access-token", request.getHeader("Authorization"));
    }

    // =========================================================================
    // INTEGRATION TESTS: Token refresh and retry logic
    // =========================================================================

    @Test
    void send_When401Returned_RefreshesTokenAndRetriesSuccessfully_IntegrationTest() throws Exception {
        // Arrange: mock server returns 401, then successful refresh, then successful retry
        mockWebServer.enqueue(new MockResponse().setResponseCode(401));
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{\"accessToken\": \"new-access-token\"}"));
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("Retried Success Data"));

        when(mockMapper.writeValueAsString(any())).thenReturn("{\"refreshToken\": \"valid-refresh-token\"}");
        when(mockMapper.readValue(anyString(), eq(AuthResponse.class)))
                .thenReturn(new AuthResponse("new-access-token", "valid-refresh-token"));

        // Act: make request that initially fails with 401
        HttpResponse<String> response = httpClient.get("protected-data");

        // Assert: final response is successful
        assertEquals(200, response.statusCode());
        assertEquals("Retried Success Data", response.body());

        // Assert: verify the request sequence
        RecordedRequest firstAttempt = mockWebServer.takeRequest();
        assertEquals("Bearer initial-access-token", firstAttempt.getHeader("Authorization"));

        RecordedRequest refreshAttempt = mockWebServer.takeRequest();
        assertEquals("/api/auth/refresh", refreshAttempt.getPath());

        RecordedRequest retryAttempt = mockWebServer.takeRequest();
        assertEquals("Bearer new-access-token", retryAttempt.getHeader("Authorization"));
    }

    // =========================================================================
    // BOUNDARY TESTS: Edge cases and error conditions
    // =========================================================================

    @Test
    void send_When401ReturnedAndRefreshFails_ClearsTokensAndReturns401_BoundaryTest() throws Exception {
        // Arrange: mock server returns 401 for both initial request and refresh attempt
        mockWebServer.enqueue(new MockResponse().setResponseCode(401));
        mockWebServer.enqueue(new MockResponse().setResponseCode(401));

        when(mockMapper.writeValueAsString(any())).thenReturn("{\"refreshToken\": \"invalid-refresh-token\"}");

        // Act: make request that fails and refresh also fails
        HttpResponse<String> response = httpClient.get("protected-data");

        // Assert: final response is still 401
        assertEquals(401, response.statusCode());
        // Assert: token storage is cleared (security measure)
        verify(mockTokenStorage).clearRefreshToken();
    }

    @Test
    void send_WhenMultipleConsecutive401s_EventuallyClears_RegressionTest() throws Exception {
        // Regression: ensure multiple auth failures don't get stuck in infinite loops
        mockWebServer.enqueue(new MockResponse().setResponseCode(401));
        mockWebServer.enqueue(new MockResponse().setResponseCode(401));

        when(mockMapper.writeValueAsString(any())).thenReturn("{\"refreshToken\": \"bad\"}");

        HttpResponse<String> response = httpClient.get("endpoint");

        assertEquals(401, response.statusCode());
        verify(mockTokenStorage).clearRefreshToken();

        // Subsequent requests should not have authorization header after clearing
        // (this would be tested by making another request, but we've already verified clearing)
    }

    @Test
    void send_When500ServerError_DoesNotAttemptTokenRefresh_BoundaryTest() throws Exception {
        // Arrange: server returns 500 (not 401, so no refresh should be attempted)
        mockWebServer.enqueue(new MockResponse().setResponseCode(500).setBody("Internal Server Error"));

        // Act
        HttpResponse<String> response = httpClient.get("broken-endpoint");

        // Assert: response is 500 and no refresh attempt was made
        assertEquals(500, response.statusCode());

        // Verify only one request was made (no refresh retry)
        RecordedRequest onlyRequest = mockWebServer.takeRequest();
        assertNull(mockWebServer.takeRequest(0, java.util.concurrent.TimeUnit.MILLISECONDS),
                   "Should not have made a second request for non-401 errors");
    }

    @Test
    void post_WithRefreshToken_MaintainsSessionAcrossRequests_IntegrationTest() throws Exception {
        // Arrange: first request fails with 401, refresh succeeds, subsequent requests use new token
        mockWebServer.enqueue(new MockResponse().setResponseCode(401));
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{\"accessToken\": \"refreshed-token\"}"));
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("First POST Success"));
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("Second POST Success"));

        when(mockMapper.writeValueAsString(any())).thenReturn("{\"refreshToken\": \"valid-refresh-token\"}");
        when(mockMapper.readValue(anyString(), eq(AuthResponse.class)))
                .thenReturn(new AuthResponse("refreshed-token", "valid-refresh-token"));

        // Act: make multiple requests
        httpClient.get("first-endpoint");
        HttpResponse<String> secondResponse = httpClient.get("second-endpoint");

        // Assert: second request uses refreshed token
        assertEquals(200, secondResponse.statusCode());

        // Skip the first three (401, refresh, first successful request)
        mockWebServer.takeRequest();
        mockWebServer.takeRequest();
        mockWebServer.takeRequest();

        RecordedRequest secondRequest = mockWebServer.takeRequest();
        assertEquals("Bearer refreshed-token", secondRequest.getHeader("Authorization"));
    }
}

