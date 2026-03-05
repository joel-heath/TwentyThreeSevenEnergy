package uk.ac.soton.comp2300.group42.energyclient.data.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;
import uk.ac.soton.comp2300.group42.common.ApiErrorResponse;
import uk.ac.soton.comp2300.group42.energyclient.domain.exception.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BaseApiClientTest {

    @Mock AuthenticatedHttpClient mockHttpClient;
    @Mock JsonMapper mockMapper;
    @Mock HttpResponse<String> mockResponse;
    @Mock ApiErrorResponse mockErrorResponse;

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
    @SuppressWarnings("unchecked")
    void get_WhenSuccessful_ReturnsDeserializedObject() throws Exception {
        when(mockHttpClient.get(anyString())).thenReturn(mockResponse);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("\"Success Data\"");

        when(mockMapper.readValue(anyString(), any(TypeReference.class))).thenReturn("Success Data");

        String result = dummyClient.fetchTestData();

        assertEquals("Success Data", result);
    }

    @Test
    void throwIfNotSuccess_WhenBodyIsNull_ThrowsDataFetchException() throws Exception {
        when(mockResponse.uri()).thenReturn(URI.create("http://0.0.0.0/"));
        when(mockHttpClient.get(anyString())).thenReturn(mockResponse);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(null);

        assertThrows(DataFetchException.class, () -> dummyClient.fetchTestData());
    }

    @Test
    void throwIfNotSuccess_WhenBodyIsEmpty_ThrowsDataFetchException() throws Exception {
        when(mockResponse.uri()).thenReturn(URI.create("http://0.0.0.0/"));
        when(mockHttpClient.get(anyString())).thenReturn(mockResponse);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("   ");

        assertThrows(DataFetchException.class, () -> dummyClient.fetchTestData());
    }

    @Test
    @SuppressWarnings("unchecked")
    void throwIfNotSuccess_WhenFailsToDeserialize_ThrowsDataFetchException() throws Exception {
        when(mockResponse.uri()).thenReturn(URI.create("http://0.0.0.0/"));
        when(mockHttpClient.get(anyString())).thenReturn(mockResponse);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn("{\"invalid-json");

        JacksonException jacksonMockException = mock(JacksonException.class);
        when(mockMapper.readValue(anyString(), any(TypeReference.class))).thenThrow(jacksonMockException);

        assertThrows(DataFetchException.class, () -> dummyClient.fetchTestData());
    }

    @Test
    void throwIfNotSuccess_WhenStatus400_ThrowsBadRequestException() throws Exception {
        when(mockHttpClient.get(anyString())).thenReturn(mockResponse);
        when(mockResponse.statusCode()).thenReturn(400);
        when(mockResponse.body()).thenReturn("API Error Response");
        when(mockMapper.readValue("API Error Response", ApiErrorResponse.class)).thenReturn(mockErrorResponse);

        assertThrows(BadRequestException.class, () -> dummyClient.fetchTestData());
    }

    @Test
    void throwIfNotSuccess_WhenStatus401_ThrowsUnauthorizedException() throws Exception {
        when(mockHttpClient.get(anyString())).thenReturn(mockResponse);
        when(mockResponse.statusCode()).thenReturn(401);
        when(mockResponse.body()).thenReturn("API Error Response");
        when(mockMapper.readValue("API Error Response", ApiErrorResponse.class)).thenReturn(mockErrorResponse);

        assertThrows(UnauthorizedException.class, () -> dummyClient.fetchTestData());
    }

    @Test
    void throwIfNotSuccess_WhenStatus403_ThrowsUnauthorizedException() throws Exception {
        when(mockHttpClient.get(anyString())).thenReturn(mockResponse);
        when(mockResponse.statusCode()).thenReturn(403);
        when(mockResponse.body()).thenReturn("API Error Response");
        when(mockMapper.readValue("API Error Response", ApiErrorResponse.class)).thenReturn(mockErrorResponse);

        assertThrows(ForbiddenException.class, () -> dummyClient.fetchTestData());
    }

    @Test
    void throwIfNotSuccess_WhenStatus404_ThrowsNotFoundException() throws Exception {
        when(mockHttpClient.get(anyString())).thenReturn(mockResponse);
        when(mockResponse.statusCode()).thenReturn(404);
        when(mockResponse.body()).thenReturn("API Error Response");
        when(mockMapper.readValue("API Error Response", ApiErrorResponse.class)).thenReturn(mockErrorResponse);

        assertThrows(NotFoundException.class, () -> dummyClient.fetchTestData());
    }

    @Test
    void throwIfNotSuccess_WhenStatus409_ThrowsConflictException() throws Exception {
        when(mockHttpClient.get(anyString())).thenReturn(mockResponse);
        when(mockResponse.statusCode()).thenReturn(409);
        when(mockResponse.body()).thenReturn("API Error Response");
        when(mockMapper.readValue("API Error Response", ApiErrorResponse.class)).thenReturn(mockErrorResponse);

        assertThrows(ConflictException.class, () -> dummyClient.fetchTestData());
    }

    @Test
    void throwIfNotSuccess_WhenStatus500_ThrowsInternalServerErrorException() throws Exception {
        when(mockHttpClient.get(anyString())).thenReturn(mockResponse);
        when(mockResponse.statusCode()).thenReturn(500);
        when(mockResponse.body()).thenReturn("API Error Response");
        when(mockMapper.readValue("API Error Response", ApiErrorResponse.class)).thenReturn(mockErrorResponse);

        assertThrows(InternalServerErrorException.class, () -> dummyClient.fetchTestData());
    }
}