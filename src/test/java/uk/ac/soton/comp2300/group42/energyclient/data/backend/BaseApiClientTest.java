package uk.ac.soton.comp2300.group42.energyclient.data.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.ApiException;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.DataFetchException;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.NetworkException;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.UnauthorizedException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BaseApiClientTest {

    @Mock AuthenticatedHttpClient mockHttpClient;
    @Mock JsonMapper mockMapper;
    @Mock HttpResponse<String> mockResponse;

    DummyApiClient dummyClient;

    private static class DummyApiClient extends BaseApiClient {
        DummyApiClient(AuthenticatedHttpClient httpClient, JsonMapper mapper) {
            super(httpClient, mapper);
        }

        public String fetchTestData() {
            return get("/test-path", new TypeReference<>() {});
        }
    }

    @BeforeEach
    void setUp() {
        dummyClient = new DummyApiClient(mockHttpClient, mockMapper);
    }

    @Test
    void get_WhenIOExceptionOccurs_ThrowsNetworkException() throws Exception {
        when(mockHttpClient.get(anyString())).thenThrow(new IOException("Simulated network failure"));

        assertThrows(NetworkException.class, () -> dummyClient.fetchTestData());
    }

    @Test
    void handleResponse_WhenStatus401_ThrowsUnauthorizedException() throws Exception {
        when(mockResponse.uri()).thenReturn(URI.create("http://0.0.0.0/"));
        when(mockHttpClient.get(anyString())).thenReturn(mockResponse);
        when(mockResponse.statusCode()).thenReturn(401);

        assertThrows(UnauthorizedException.class, () -> dummyClient.fetchTestData());
    }

    @Test
    void handleResponse_WhenStatus500_ThrowsApiException() throws Exception {
        when(mockResponse.uri()).thenReturn(URI.create("http://0.0.0.0/"));
        when(mockHttpClient.get(anyString())).thenReturn(mockResponse);
        when(mockResponse.statusCode()).thenReturn(500);

        ApiException exception = assertThrows(ApiException.class, () -> dummyClient.fetchTestData());
        assertEquals(500, exception.getStatusCode());
    }

    @Test
    void handleResponse_WhenBodyIsEmpty_ThrowsDataFetchException() throws Exception {
        when(mockResponse.uri()).thenReturn(URI.create("http://0.0.0.0/"));
        when(mockHttpClient.get(anyString())).thenReturn(mockResponse);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("   ");

        assertThrows(DataFetchException.class, () -> dummyClient.fetchTestData());
    }

    @Test
    @SuppressWarnings("unchecked")
    void handleResponse_WhenFailsToDeserialize_ThrowsDataFetchException() throws Exception {
        when(mockResponse.uri()).thenReturn(URI.create("http://0.0.0.0/"));
        when(mockHttpClient.get(anyString())).thenReturn(mockResponse);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("{\"invalid-json");

        JacksonException jacksonMockException = mock(JacksonException.class);
        when(mockMapper.readValue(anyString(), any(TypeReference.class))).thenThrow(jacksonMockException);

        assertThrows(DataFetchException.class, () -> dummyClient.fetchTestData());
    }

    @Test
    @SuppressWarnings("unchecked")
    void get_WhenSuccessful_ReturnsDeserializedObject() throws Exception {
        when(mockHttpClient.get(anyString())).thenReturn(mockResponse);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("\"Success Data\"");
        
        when(mockMapper.readValue(anyString(), any(TypeReference.class))).thenReturn("Success Data");

        String result = dummyClient.fetchTestData();

        assertEquals("Success Data", result);
    }
}