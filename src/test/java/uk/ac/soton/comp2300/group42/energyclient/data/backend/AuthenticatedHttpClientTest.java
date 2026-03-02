package uk.ac.soton.comp2300.group42.energyclient.data.backend;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.json.JsonMapper;
import uk.ac.soton.comp2300.group42.energyclient.data.security.TokenStorageService;
import uk.ac.soton.comp2300.group42.user.AuthResponse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticatedHttpClientTest {

    @Mock TokenStorageService mockTokenStorage;
    @Mock JsonMapper mockMapper;
    MockWebServer mockWebServer;

    AuthenticatedHttpClient httpClient;

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

    @Test
    void get_IncludesAuthorizationHeader() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("Success Data"));

        HttpResponse<String> response = httpClient.get("data");

        assertEquals(200, response.statusCode());
        assertEquals("Success Data", response.body());

        RecordedRequest request = mockWebServer.takeRequest();
        assertEquals("GET", request.getMethod());
        assertEquals("/api/data", request.getPath());
        assertEquals("Bearer initial-access-token", request.getHeader("Authorization"));
    }

    @Test
    void send_When401Returned_RefreshesTokenAndRetriesSuccessfully() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(401));
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("{\"accessToken\": \"new-access-token\"}"));
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody("Retried Success Data"));

        when(mockMapper.writeValueAsString(any())).thenReturn("{\"refreshToken\": \"valid-refresh-token\"}");
        when(mockMapper.readValue(anyString(), eq(AuthResponse.class)))
                .thenReturn(new AuthResponse("new-access-token", "valid-refresh-token"));

        HttpResponse<String> response = httpClient.get("protected-data");

        assertEquals(200, response.statusCode());
        assertEquals("Retried Success Data", response.body());

        RecordedRequest firstAttempt = mockWebServer.takeRequest();
        assertEquals("Bearer initial-access-token", firstAttempt.getHeader("Authorization"));

        RecordedRequest refreshAttempt = mockWebServer.takeRequest();
        assertEquals("/api/auth/refresh", refreshAttempt.getPath());

        RecordedRequest retryAttempt = mockWebServer.takeRequest();
        assertEquals("Bearer new-access-token", retryAttempt.getHeader("Authorization"));
    }

    @Test
    void send_When401ReturnedAndRefreshFails_ClearsTokens() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(401));
        mockWebServer.enqueue(new MockResponse().setResponseCode(401));

        when(mockMapper.writeValueAsString(any())).thenReturn("{\"refreshToken\": \"invalid-refresh-token\"}");

        HttpResponse<String> response = httpClient.get("protected-data");

        assertEquals(401, response.statusCode());
        verify(mockTokenStorage).clearRefreshToken();
    }
}